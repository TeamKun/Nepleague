# Nepleague

## Commands
### TeamCommand
```
/nep team add <InternalTeamName> (<DisplayTeamName>)
/nep team remove <InternalTeamName>
/nep team list  // For Debug
```

### StartCommand
```
/nep start <string>
```
<string>が正解のゲームを開始
  
### ConfigCommand
```
/nep config <ConfigName> <Data>
```
(Tab補完出るので)

### Finish Command
```
/nep finish
```
ゲーム終了します(結果発表前に必要です)

### Result Command
```
/nep result chat
/nep result title
```
チャット/タイトル結果発表モードになります
(どんなクリックも反応するので気を付けて)

### Reset Command
```
/nep reset
```
！！！！！！！毎ゲーム終了後に必要です！！！！！！！

### Input Command
```
/nep input <InternalTeamName> <Index> <playerSelector>
```
Indexは0始まりではなく1始まりです
例)1文字目 -> 1
