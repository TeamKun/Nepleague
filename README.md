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