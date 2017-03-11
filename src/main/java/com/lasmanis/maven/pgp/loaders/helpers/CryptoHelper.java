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

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Interface for wrapping crypto libraries
 * 
 * @author mpl
 */
public interface CryptoHelper 
{
    /**
     * Decrypt the given cipherText
     * 
     * @param cipherText the cipher text
     * @return the plain text
     * @throws org.apache.maven.plugin.MojoExecutionException on any error
     */
    String decrypt(String cipherText)
            throws MojoExecutionException;
}
