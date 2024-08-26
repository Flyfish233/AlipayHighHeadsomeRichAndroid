# 支付宝装X模块

Xposed module for Alipay App which makes you barcode payment interface same as Diamond members or
customized skins.

这是一个Xposed模块，它可以让你的支付宝付款背景为黑色钻石会员样式或自定义皮肤。

*该模块作者仅在LSPosed官方同步，其他途径下载请自行分析风险。*

### 界面

欢迎PR界面操作，比如下载SD.zip文件以及权限Require,由于作者并不是专业Android开发，针对SDK API
23及其他尝试了一些放弃了界面傻瓜操作。

另外觉得好用请右上角Star ❥这个Repo，^_^

### 版本

#### 个人修改

**由于改动较大，暂时不准备 Pull Request，也不提供安装包，自己打包才放心，使得您的支付宝账户安全无虞**

- 3.0.0 (测试支付宝 10.6.30) 2024.8

    1. 主要代码使用 Kotlin 重构，实现全部功能
    2. 修掉了 Gradle 脚本，在 Release 下可以开启混淆和代码缩减
    3. FastJson 替换为最新 FastJSON2
    4. MainActivity 不再桌面显示，改为在 Xposed 中显示 (若可以提高 API 限制，可考虑使用 Compose
       写新的，以后再说)

| 版本    | 功能                         | 最后更新时间  | 是否可用                    |
|-------|----------------------------|---------|-------------------------|
| 2.2.8 | 余额宝修改及收益自动计算、 自由选择支付宝付款码背景 | 2018.06 | <=10.0.12               |
| 2.2.8 | 自由选择支付宝付款码背景               | 2018.06 | <10.2.33                |
| 2.3.0 | 仅钻石付款背景                    | 2021.09 | 所有版本                    |
| 2.4.0 | 钻石背景或自定义皮肤                 | 2022.01 | >=10.2.23, （皮肤<10.5.53） |
| 2.4.2 | 钻石背景或自定义皮肤(含导出)            | 2023.09 | >=10.2.23, （皮肤<10.5.53） |
| 2.5.0 | 钻石背景或自定义皮肤(含导出)            | 2023.11 | >=10.2.23               |

### 自定义皮肤说明

- 手动下载DEMO资源包，放入SD卡对应程序文件目录。(**[SD CARD]**
  \Android\media\com.eg.android.AlipayGphone\)
- 如果你没有看到**[SD CARD]**
  \Android\media\com.eg.android.AlipayGphone\目录，在插件已经安装的情况下，打开支付宝付款二维码，程序会自动创建一个）
- **
  更新支付宝或者清除支付宝缓存后，需要在000_HOHO_ALIPAY_SKIN里重新创建update文件夹（或文件）以更新缓存。  **
- **第一次使用需手动修改支付宝权限给与存储卡读写功能以读取自定义界面配置（旧版安卓系统）**

### 目录结构说明

以下提到的所有"目录"均为000_HOHO_ALIPAY_SKIN下的目录。

| 命名      | 含义             | 是否目录 | 手动创建 | 作用后文件消失 |
|---------|----------------|------|------|---------|
| actived | 开启自定义皮肤功能      | 随意   | 是    | 否       |
| update  | 触发支付宝增量自定义皮肤缓存 | 随意   | 是    | 是       |
| delete  | 触发支付宝删除自定义皮肤缓存 | 随意   | 是    | 是       |
| export  | 导出支付宝拥有皮肤      | 随意   | 是    | 是       |
| 任意名称目录  | 自定义皮肤文件夹       | 是    | 是    | 否       |
| 任意名称文件  | 无作用            | 否    | 是    | 否       |

> 通常情况下，创建 update文件夹 时，请同时创建 delete文件夹 干净清除。

> 触发仅在展示二维码时有效

> 自定义皮肤开启后，账号皮肤数据不会被修改或影响，仅本地切换。

> export文件夹建立后，展示二维码，成功便export文件夹消失，如需更新展示则仍然需要创建update或delete文件夹。

> 多目录存在时多个皮肤之间随机数切换，没有去重，看你手机心情展示。

***资源包已经包含3个作者画的DEMO与简单的PSD文件，请自行研究。***

## 快速手机修改背景步骤

1. 打开任意目录下background_2x1.png文件，修改图片为手机分辨率大小并替换图片。也可直接替换文件。

> <img src="https://raw.githubusercontent.com/nov30th/AlipayHighHeadsomeRichAndroid/master/images/ps_bg.png" height="600" />

2. 在000_HOHO_ALIPAY_SKIN目录下创建delete与update两个文件夹。
3. 打开支付宝付款码即可看到效果如图。

> <img src="https://raw.githubusercontent.com/nov30th/AlipayHighHeadsomeRichAndroid/master/images/final_code.png" height="600" />

4. 其他界面内元素同样。如不希望随机界面，删除000_HOHO_ALIPAY_SKIN下其他皮肤目录后，同样创建创建delete与update两个文件夹触发更新。

## 更新历史

[更新历史](Updates.md)

【作者自用】
