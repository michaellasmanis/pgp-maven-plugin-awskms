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
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.KeyUnavailableException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.maven.plugin.MojoExecutionException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link AwsCryptoHelper}
 * 
 * @author mpl
 */
@RunWith(MockitoJUnitRunner.class)
public class AwsCryptoHelperTest 
{
    /**
     * object under test
     */
    private AwsCryptoHelper instance;

    /**
     * Mock of aws client
     */
    @Mock
    private AWSKMS kms;

    /**
     * arguement captor for AWSKMS.decrypt()
     */
    @Captor
    ArgumentCaptor<DecryptRequest> kmsCaptor;

    /**
     * test cipher text (not really valid)
     */
    private final String cipherText = "Zm9vYmFy"; 

    /**
     * test plain text
     */
    private final String plainText = "foobar";

    /**
     * test cipher text (not really valid)
     */
    private final String exceptionText = "ZXhjZXB0aW9u"; 
    
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
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        // setup our instance (don't use the mock)
        this.instance = new AwsCryptoHelper(this.kms);
    }
    
    /**
     * Test level cleanup
     */
    @After
    public void tearDown() {
    }
    
    /**
     * test bad input
     * @throws java.lang.Exception
     */
    @Test
    public void testInput() throws Exception
    {
        // setup our instance
        this.instance = new AwsCryptoHelper(this.kms);
        
        // null/blank cipher text
        assertThatThrownBy(() -> {this.instance.decrypt(null);})
                .isInstanceOf(MojoExecutionException.class);
        assertThatThrownBy(() -> {this.instance.decrypt("");})
                .isInstanceOf(MojoExecutionException.class);

        // invalid cipherText
        assertThatThrownBy(() -> {this.instance.decrypt(this.cipherText.substring(0, this.cipherText.length()-2));})
                .isInstanceOf(MojoExecutionException.class);
        assertThatThrownBy(() -> {this.instance.decrypt("*" + this.cipherText.substring(1));})
                .isInstanceOf(MojoExecutionException.class);

        verify(this.kms, times(0)).decrypt(any());
    }

    /**
     * test kms exception
     */
    @Test
    public void testKmsException() throws Exception
    {
        // setup the mock
        when(this.kms.decrypt(any()))
                .thenThrow(KeyUnavailableException.class);

        assertThatThrownBy(() -> {this.instance.decrypt(this.cipherText);})
                .isInstanceOf(MojoExecutionException.class);
        verify(this.kms).decrypt(any());
        
    }

    /**
     * Let's check a Mocked decrypt
     * @throws java.lang.Exception
     */
    @Test
    public void testMockedDecrypt() throws Exception
    {
        // setup the mock
        final DecryptResult dr = new DecryptResult();
        dr.setPlaintext(ByteBuffer.wrap(this.plainText.getBytes(StandardCharsets.UTF_8)));
        when(this.kms.decrypt(any()))
                .thenReturn(dr);

        assertThat(this.instance.decrypt(this.cipherText))
                .isEqualTo(this.plainText);
        verify(this.kms).decrypt(any());
    }
}
