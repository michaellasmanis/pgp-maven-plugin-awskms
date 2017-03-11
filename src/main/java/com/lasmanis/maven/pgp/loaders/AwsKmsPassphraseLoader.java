/**
 * Copyright © 2017 Michael Lasmanis (michael@lasmanis.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lasmanis.maven.pgp.loaders;

import com.lasmanis.maven.pgp.loaders.helpers.AwsCryptoHelper;
import com.lasmanis.maven.pgp.loaders.helpers.CryptoHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.kohsuke.maven.pgp.PassphraseLoader;
import org.kohsuke.maven.pgp.PgpMojo;
import java.io.IOException;
import java.util.Map;

/**
 * Decryption wrapper for a pass phrase via AWS KMS
 * 
 * @author mpl
 */
@Component(role=PassphraseLoader.class,hint="awskms")
public class AwsKmsPassphraseLoader
    extends PassphraseLoader
{
    /**
     * all the loaders that Plexus knows about
     * */
    @Requirement(role=PassphraseLoader.class)
    private Map<String, PassphraseLoader> loaders;

    /**
     * Our crypto helper
     */
    private final CryptoHelper crypto;
    
    /**
     * Constructor
     */
    public AwsKmsPassphraseLoader()
    {
        super();
        this.crypto = new AwsCryptoHelper();
    }

    /**
     * Constructor for unit testing
     * 
     * This constructors allows the map of loaders to
     * be initialized by the test harness.  This is normally
     * handled directly by Plexus.
     * 
     * @param loaders the Map of loaders
     * @param crypto the crypto helper
     */
    AwsKmsPassphraseLoader(
            final Map<String, PassphraseLoader> loaders,
            final CryptoHelper crypto)
    {
        super();
        this.loaders = loaders;
        this.crypto = crypto;
    }

    @Override
    public String load(
            final PgpMojo pm, 
            final PGPSecretKey pgpsk, 
            final String string) 
            throws IOException, 
                MojoExecutionException 
    {
        // check
        if (string == null || string.isEmpty())
        {
            throw new MojoExecutionException("Scheme 'awskms:' expects an additional specifier.");
        }

        // make sure we have crypto support
        if (this.crypto == null)
        {
            throw new MojoExecutionException("Failed to initialize the crypto library.");
        }

        // extract the scheme
        int head = string.indexOf(':');
        if (head<0)
        {
            throw new MojoExecutionException("Invalid additional specifier. It needs to start with a scheme like 'FOO:': " + string);
        }
        String scheme = string.substring(0, head);
        
        // get the loader
        PassphraseLoader loader = null;
        if (this.loaders != null)
        {
            loader = this.loaders.get(scheme);
        }
        if (loader == null)
        {
            throw new MojoExecutionException("Invalid passphrase scheme '" + scheme + "'. If this is your custom scheme, perhaps you forgot to specify it in <dependency> to this plugin?");            
        }

        // get the cipherText
        String cipherText = loader.load(pm, pgpsk, string.substring(head+1));
        
        // decrypt the pass phrase
        return this.crypto.decrypt(cipherText);
    }
}
