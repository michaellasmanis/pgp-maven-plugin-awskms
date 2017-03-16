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

import com.lasmanis.maven.pgp.loaders.helpers.CryptoHelper;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.maven.pgp.PassphraseLoader;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link AwsKmsPassphraseLoader}
 * 
 * @author mpl
 */
@RunWith(MockitoJUnitRunner.class)
public class AwsKmsPassphraseLoaderTest 
{
    /**
     * object under test
     */
    private AwsKmsPassphraseLoader instance;
    
    /**
     * mock of loader for scheme "foo:"
     */
    @Mock
    private PassphraseLoader fooLoader;

    /**
     * mock of loader for scheme "bar:"
     */
    @Mock
    private PassphraseLoader barLoader;

    /**
     * mock of the crypto helper
     */
    @Mock
    private CryptoHelper crypto;
    
    // test setup

    /**
     * Class level initializer
     */
    @BeforeClass
    public static void setUpClass() {
    }
    
    /**
     * Class level cleanup
     */
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test level initializer
     * @throws java.lang.Exception on error
     */
    @Before
    public void setUp() throws Exception {
        // setup our mocks
        when(this.fooLoader.load(any(), any(), anyString()))
                .thenReturn("oof");
        when(this.barLoader.load(any(), any(), anyString()))
                .thenReturn("rab");
        
        when(this.crypto.decrypt("oof"))
                .thenReturn("foo");
        when(this.crypto.decrypt("rab"))
                .thenReturn("bar");
        
        // setup our instance
        Map<String, PassphraseLoader> l = new HashMap<>();
        l.put("foo", fooLoader);
        l.put("bar", barLoader);
        l.put("null", null);
        this.instance = new AwsKmsPassphraseLoader(l,this.crypto);
    }
    
    /**
     * Test level cleanup
     */
    @After
    public void tearDown() {
    }

    /**
     * Test inputs
     */
    @Test
    public void testInputErrors()
    {
        assertThatThrownBy(() -> {this.instance.load(null,null,null);})
                .isInstanceOf(MojoExecutionException.class);
        assertThatThrownBy(() -> {this.instance.load(null,null,"");})
                .isInstanceOf(MojoExecutionException.class);
        assertThatThrownBy(() -> {this.instance.load(null,null,"foo");})
                .isInstanceOf(MojoExecutionException.class);
    }

    /**
     * Test loader lookups
     */
    @Test
    public void testLoaderLookupExceptions()
    {
       // should fail because we haven't setup this one yet
        assertThatThrownBy(() -> {this.instance.load(null,null,"my:loader");})
                .isInstanceOf(MojoExecutionException.class);

        // should fail because it is a bad entry
        assertThatThrownBy(() -> {this.instance.load(null,null,"null:loader");})
                .isInstanceOf(MojoExecutionException.class);

        // empty the loader map, existing ones should fail now too
        this.instance = new AwsKmsPassphraseLoader(new HashMap<>(), this.crypto);
        assertThatThrownBy(() -> {this.instance.load(null, null, "foo:bar");})
                .isInstanceOf(MojoExecutionException.class);

        // null the loader map, existing ones should fail now too
        this.instance = new AwsKmsPassphraseLoader(null, this.crypto);
        assertThatThrownBy(() -> {this.instance.load(null, null, "foo:bar");})
                .isInstanceOf(MojoExecutionException.class);
    }

    /**
     * Test failed crypto setup
     */
    @Test
    public void testNullCrypto()
    {
        Map<String, PassphraseLoader> l = new HashMap<>();
        l.put("foo", fooLoader);
        l.put("bar", barLoader);
        l.put("null", null);
        this.instance = new AwsKmsPassphraseLoader(l,null);
        assertThatThrownBy(() -> {this.instance.load(null, null, "foo:bar");})
                .isInstanceOf(MojoExecutionException.class);
    }

    /**
     * Test basic operation
     * @throws java.lang.Exception on error
     */
    @Test
    public void testBasicOperation() throws Exception
    {
        assertThat(this.instance.load(null, null, "foo:bar"))
                .isEqualTo("foo");
        assertThat(this.instance.load(null, null, "bar:foo"))
                .isEqualTo("bar");
        
        verify(this.fooLoader).load(null, null, "bar");
        verify(this.crypto).decrypt("oof");
        verify(this.barLoader).load(null, null, "foo");
        verify(this.crypto).decrypt("rab");
    }
}
