# MiniGamesBox

Library box with massive content that could be seen as minigames core.
**The name is not final and may change...**

## JavaDocs

https://jd.plugily.xyz/minigamesbox

## Maven repo

Add repository

```xml

<repositories>
    <repository>
        <id>plugily-projects</id>
        <url>https://maven.plugily.xyz</url>
    </repository>
</repositories>
```

Then add the dependency, select one of 3 modules: classic, minecraft, database

```xml

<dependencies>
    <dependency>
        <groupId>plugily.projects</groupId>
        <artifactId>minigamesbox(-inventory/-classic)</artifactId>
        <version>1.1.1</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

# Credits

## Open Source Libraries

| Library                                              | Author                                        | License                                                                       | Used on module         |
|------------------------------------------------------|-----------------------------------------------|-------------------------------------------------------------------------------|------------------------|
| [FastInv](https://github.com/MrMicky-FR/FastInv)     | [MrMicky](https://github.com/MrMicky-FR)      | [MIT License](https://github.com/MrMicky-FR/FastInv/blob/master/LICENSE)      | MiniGamesBox Inventory |
| [Commons Box](https://github.com/Plajer/Commons-Box) | [Plajer](https://github.com/Plajer)           | [GPLv3](https://github.com/Plajer/Commons-Box/blob/master/LICENSE.md)         | MiniGamesBox Classic   |
| [XSeries](https://github.com/CryptoMorin/XSeries)    | [CryptoMorin](https://github.com/CryptoMorin) | [MIT License](https://github.com/CryptoMorin/XSeries/blob/master/LICENSE.txt) | MiniGamesBox           |
