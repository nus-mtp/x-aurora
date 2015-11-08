Chrome Plugin Test ver 0.5：

- Establish connection with background programm.
- Can send webpage content to background after the page finish loading. (content will be casted to UTF-8)
- Can detect duplicated URL.

* IMPORTANT *
When reloading the plugin, the content script DOES not refresh.
So you need to refresh those pages to let them to generate new content pages in order to work.

（和上面的英文内容差不多）
试制Chrome插件3号(ver 0.5)：

可以和后台部分建立连接。
可以在网页加载完成时向后台发送网页内容。(内容会转换成UTF-8送到后台。)
可以过滤重复的URL。

* 重要的事 *
当重新载入插件时，已打开的页面对应的Content Script不会刷新！
所以需要将页面刷新之后，Content Script才会刷新，才能产生效果。