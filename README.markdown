AWS KMS support for pgp-maven-plugin
====================================

Overview
--------

This plugin adds support to [pgp-maven-plugin](http://kohsuke.org/pgp-maven-plugin/) for decrypting a pgp passphrase via [AWS Key Management Service](https://aws.amazon.com/kms/).  It wraps other
passphrase loaders with KMS decryption.

Usage
-----

You need to configure your pom.xml to add the pgp-maven-plugin-awskms as a dependency to pgp-maven-plugin.

    <plugin>
        <groupId>org.kohsuke</groupId>
        <artifactId>pgp-maven-plugin</artifactId>
        <version>1.1</version>
        <dependencies>
            <dependency>
                <groupId>com.lasmanis</groupId>
                <artifactId>pgp-maven-plugin-awskms</artifactId>
                <version>0.0.1</version>
            </dependency>
        </dependencies>
        <configuration>
            <secretkey>SECRET_KEY_SPECIFIER</secretkey>
            <passphrase>PASSPHRASE_SPECIFIER</passphrase>
        </configuration>
        <executions>
            <execution>
                <goals>
                    <goal>sign</goal>
                </goals>
            </execution>
        </executions>
    </plugin>

*Specifiy the passphrase cipher text as literal text*

    awskms:literal:ENCRYPTEDPASSPHRASE

This lets you specify the encrypted passphrase inline directly.

*Store the passphrase cipher text is a file*

    awskms:file:PATH/TO/FILE

This lets you specify store the encrypted passphrase in the local filesystem.

*Wrapping any PassphraseLoader (including custom loaders)*

    awskms:SCHEME:PATH/TO/FILE

This lets you call any included PassphraseLoader configured in the pom (built-in and custom).

AWS Credentials
---------------

Currently, the plugin uses the default credential handler from the aws-java-sdk.  Configuration and initialization priorities are described [here](http://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html).  The ability to supply credentials via the PASSPHRASE_SPECIFIER is coming in a future release.

Documentation
-------------

* [JavaDocs Site](https://michaellasmanis.github.io/pgp-maven-plugin-awskms/)
* [Api Docs](https://michaellasmanis.github.io/pgp-maven-plugin-awskms/apidocs/)

License
-------

Copyright 2017 Michael Lasmanis.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


