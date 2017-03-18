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
package com.lasmanis.maven.pgp.loaders.helpers;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.util.Base64;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Wrapper around AWS Crypto library.
 *
 * @author mpl
 */
public class AwsCryptoHelper
        implements CryptoHelper {
    /**
     * the aws client.
     */
    final private AWSKMS client;

    /**
     * Public constructor.
     */
    public AwsCryptoHelper() {
        this.client = AWSKMSClientBuilder.defaultClient();
    }

    /**
     * Constructor for unit testing.
     *
     * @param client aws client
     */
    AwsCryptoHelper(final AWSKMS client) {
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public String decrypt(final String cipherText)
            throws MojoExecutionException {
        // check
        if (cipherText == null || cipherText.isEmpty()) {
            throw new MojoExecutionException("Empty cipherText.");
        }

        // parse the cipher text
        final byte[] ciphertextBytes;
        try {
            ciphertextBytes = Base64.decode(cipherText);
        } catch (final IllegalArgumentException ex) {
            throw new MojoExecutionException(
                    "Invalid base 64 in cipherText", ex);
        }

        // decrypt
        try {
            DecryptRequest req = new DecryptRequest()
                    .withCiphertextBlob(ByteBuffer.wrap(ciphertextBytes));
            ByteBuffer plainText = this.client.decrypt(req).getPlaintext();
            String ret = new String(plainText.array(), StandardCharsets.UTF_8);

            return ret;
        } catch (final Exception ex) {
            throw new MojoExecutionException(
                    "Failed to decrypt cipherText", ex);
        }
    }
}
