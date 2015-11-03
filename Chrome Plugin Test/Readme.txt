Chrome Plugin Test ver 0.2：

Should be establish connection with background logic.
Should send content upon tab switch.

Current configuration is not what we want, I will improve it again.

* IMPORTANT *
When reloading the plugin, the content script DOES not refresh.
So you need to refresh those pages to let them to generate new content pages in order to work.

（和上面的英文内容差不多）
试制Chrome插件2号：

应该——
可以和后台部分建立连接。
可以在Chrome切换标签页的时候向后台发送网页内容。

现在的判定条件还需要改进。以及大概需要把内容转换成Unix编码 —— 我在自己试验的时候Java那边收到的都是乱码

* 重要的事 *
当重新载入插件时，已打开的页面对应的Content Script不会刷新！
所以需要将页面刷新之后，Content Script才会刷新，才能产生效果。