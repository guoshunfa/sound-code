
<img width="180" src="https://cdn.jsdelivr.net/gh/guoshunfa/files/panda/202109101822245.png" alt="logo" data-canonical-src="https://cdn.jsdelivr.net/gh/guoshunfa/files/panda/202109101822245.png" style="max-width: 100%;">

![star](https://img.shields.io/github/stars/guoshunfa/sound-code) ![github](https://img.shields.io/github/forks/guoshunfa/sound-code) 

# 欢迎来到源码仓库

- [熊猫 code](https://pandacode.cn)
- [源码解读](https://doc.pandacode.cn/pages/04607b/)

# 介绍

目的：能够在线的分析/阅读源码。

内容：主要存储热门源码，可以理解为源码仓库。

阅读：结合github web ide进行在线浏览源码。

# 阅读

目前使用的是[sourcegraph](https://sourcegraph.com/)提供的平台，可以在线浏览代码，在此致谢。

## sourcegraph使用规则

拼接到查询规则后，如：`repo:^github\.com/guoshunfa/sound-code$ file:ArrayList`

```text
type: diff/commit/...
repo: regexp-pattern
rev: revision
file: regexp-pattern
lang: language-name
select: result-types
count: N/all
content: "pattern"
fork: yes/only
after: "last week"
```
