Usage
=====

You need to configure your pom.xml to add the pgp-maven-plugin-awskms as a dependency to pgp-maven-plugin.

    <plugin>
        <groupId>org.kohsuke</groupId>
        <artifactId>pgp-maven-plugin</artifactId>
        <dependencies>
            <dependency>
                <groupId>com.lasmanis</groupId>
                <artifactId>pgp-maven-plugin-awskms</artifactId>
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
