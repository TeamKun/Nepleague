# Nepleague

## Commands

### TeamCommand

```
/nep team add <InternalTeamName>
/nep team remove <InternalTeamName>
/nep team list  // For Debug
```

### StartCommand

```
/nep start <answerType> <string>
```

`<answerType>` は `HIRAGANA` `KATAKANA` `NONE`のどれか<br>
`<string>`が正解のゲームを開始

### ConfigCommand

```
/nep config <ConfigName> <Data>
```

(Tab補完出るので)

### Input Command

```
/nep input <InternalTeamName> <Index> <Char>
```

Indexは1始まりではなく0始まりです
例)1文字目 -> 0

### Remote Input Command

```
/nep rinput <Selector> <Index>
```

チャットから入力できるようになります(コマブロに仕込んでください)

## LICENCE

This software contains a font file licenced under SIL Open Font License, Version 1.1., which can be found
at [SIL Open Font License](https://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web).

THE LICENCE OF THE FONT FILE IS KEPT AS THE SAME AS THE LICENCE OF THE FONT FILE.

THE REST OF THIS PROJECT(Source) IS LICENCED UNDER THE [MPL-2.0](https://www.mozilla.org/en-US/MPL/2.0/).

このソフトウェアには、[SIL Open Font License](https://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web)の1.1版でライセンスされているファイルが含まれています。

このファイルはオリジナルのファイルと同様にSIL Open Font Licenseによってライセンスされています。

残りのソースコードは、[MPL-2.0](https://www.mozilla.org/en-US/MPL/2.0/)ライセンスに従います。