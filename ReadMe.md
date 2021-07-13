<h1>Delombok IntelliJ-based IDEs Plugin</h1>
<h2>Let you work with people who don't use lombok.</h2>
--- English ---
<p>Let you continue to use lombok and can cooperate with people who do not use it, keep the code concise and complete, and let you focus more on the core code.</p>
<br>
<p>Use: </p>
<ul>
<li>You can check "Delombok code" checkbox in vcs before committing the code.</li>
<li>You can click "Delombok project" in "Build" menu.</li>
<li>Before Delombok, the project source file will be automatically backed up to the project path/delombok/src-bak, and the project file can be restored here.</li>
</ul>
<br>
--- Chinese ---
<h3>让你使用lombok与不使用lombok插件的人合作, 保证代码完整性的同时隐藏样板式代码, 让你只关注核心代码</h3><br>
<p>使用: </p>
<ul>
<li>在提交代码前, 在vcs中勾选 "Delombok code" 复选框, 即可对要提交的代码进行delombok</li>
<li>可以在 "Build" 菜单中点击 "Delombok project" 来delombok整个项目</li>
<li>在Delombok前, 会自动将项目源文件备份至 项目路径/delombok/src-bak, 可在此处恢复项目文件</li>
</ul>
<hr>

> 解决了什么问题？
- 保留lombok的所有优点的同时, 解决lombok公认的两个问题: 
  1. 让开发人员无感知保证代码完整性 2. 让你的代码摆脱对插件的依赖, 无需再强迫团队成员安装插件
> 什么是无感知？
- delombok前后的代码看上去没什么差别, 代码依旧简洁, 依旧让你只关注核心代码的开发;
- 不改变你的编码习惯, 你依旧可以使用任何lombok注解提高编码效率, 而团队成员无需lombok插件也可正常编译运行代码;
- 操作简单, 只需在提交代码时勾选 "Delombok code", 像勾选格式化代码一样简单, 你所提交的代码都会变成完整代码, 不再依赖插件。

![delombok前后对比](https://github.com/04637/delombok/blob/master/otherR/4.png) 
![提交代码前勾选](https://github.com/04637/delombok/blob/master/otherR/3.png)               
