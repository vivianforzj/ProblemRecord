### 问题描述

在执行脚本的时候，总是报某行 `“Syntax error: "(" unexpected (expecting "fi")” `错误，但是把脚本中的这行代码拷贝出来直接在 terminal 中执行并不会报错，查了一下脚本没有发现错误。

### 解决方法

网上搜了一下，说可能是 sh 的链接问题。

- 首先查看 sh 当前链接：ls -l /bin/sh，结果如下：

   `lrwxrwxrwx 1 root root 4  5月  5  2016 /bin/sh -> dash`

  sh 默认是指向 dash 的，Ubuntu安装时默认使用dash（我是用的 ubuntu）。
  
- 然后，就是改变 sh 的链接，执行如下：

  `sudo dpkg-reconfigure dash`

  会弹出重新配置 dash 界面，并选择 “no” 不使用 dash 即可。
