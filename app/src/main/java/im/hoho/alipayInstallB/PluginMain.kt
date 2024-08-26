package im.hoho.alipayInstallB

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Environment
import com.alibaba.fastjson2.JSON
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.io.File

/**
 * Created by qzj_ on 2016/5/9.
 * Rewrite by Flyfish233 on 2024/8/27.
 */
class PluginMain : IXposedHookLoadPackage {

    @SuppressLint("SdCardPath")
    companion object {
        private const val PACKAGE_NAME = "com.eg.android.AlipayGphone"
        private const val PACKAGE_PATH = "/data/data/$PACKAGE_NAME"
        private const val ALIPAY_SKINS_ROOT = "/files/onsitepay_skin_dir"
        private const val HOHO_SKIN_SUB_FOLDER = "HOHO"
        private const val FULL_SKIN_PATH = "$PACKAGE_PATH$ALIPAY_SKINS_ROOT/$HOHO_SKIN_SUB_FOLDER"
        private val BASE_PATH_UPDATES =
            "${Environment.getExternalStorageDirectory()}/Android/media/$PACKAGE_NAME"
        private val FIXED_PATH_UPDATES = "$BASE_PATH_UPDATES/000_HOHO_ALIPAY_SKIN"
    }

    init {
        XposedBridge.log("Now Loading HOHO Alipay plugin...")
        XposedBridge.log("Powered by HOHO 20230927 杭州亚运会版 sd source changed 20231129")
    }

    @Throws(Throwable::class)
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (PACKAGE_NAME == lpparam.packageName) {
            XposedBridge.log("Loaded App: ${lpparam.packageName}")
            val isDbUpdated = booleanArrayOf(false)
            hookUserLoginResult(lpparam)
            hookActivityOnCreate(isDbUpdated)
            hookSkinConfiguration(lpparam)
        }
    }

    private fun hookUserLoginResult(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedHelpers.findAndHookMethod("com.alipay.mobilegw.biz.shared.processer.login.UserLoginResult",
            lpparam.classLoader,
            "getExtResAttrs",
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    XposedBridge.log("Now, let's install B...")
                    val map = (param.result as Map<*, *>).toMutableMap()

                    if ("memberGrade" in map) {
                        XposedBridge.log("Original member grade: ${map["memberGrade"]}")
                        map["memberGrade"] = "diamond"
                        XposedBridge.log("Member grade changed to: ${map["memberGrade"]}")
                    } else {
                        XposedBridge.log("Cannot get the member grade in return value...WTF?")
                    }
                }
            })
    }

    private fun hookActivityOnCreate(isDbUpdated: BooleanArray) {
        XposedHelpers.findAndHookMethod(Activity::class.java,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    if (isDbUpdated[0]) return
                    val context = param.thisObject as Context
                    val dbFile = context.getDatabasePath("alipayclient.db")
                    if (dbFile.exists()) {
                        XposedBridge.log("Get Database: ${dbFile.parentFile}")
                        try {
                            SQLiteDatabase.openDatabase(
                                dbFile.path, null, SQLiteDatabase.OPEN_READWRITE
                            ).use { db ->
                                db.execSQL("UPDATE 'main'.'userinfo' SET 'memberGrade' = 'diamond'")
                                XposedBridge.log("Database update successful!")
                            }
                        } catch (e: Exception) {
                            XposedBridge.log("Database update error: $e")
                        }
                    } else {
                        XposedBridge.log("Cannot get database, Ignore!")
                    }
                    isDbUpdated[0] = true
                }
            })
    }

    private fun hookSkinConfiguration(lpparam: XC_LoadPackage.LoadPackageParam) {
        val ospSkinModel = XposedHelpers.findClass(
            "com.alipay.mobile.onsitepaystatic.skin.OspSkinModel", lpparam.classLoader
        )

        XposedHelpers.findAndHookMethod("com.alipay.mobile.onsitepaystatic.ConfigUtilBiz",
            lpparam.classLoader,
            "getFacePaySkinModel",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val skinPathFile = File(FULL_SKIN_PATH)
                    File("$FIXED_PATH_UPDATES/export").checkAndDelete { exportSkins() }
                    skinPathFile.manageSkinUpdateAndDelete()
                    if (skinPathFile.exists() && File("$FIXED_PATH_UPDATES/actived").exists()) {
                        updateSkins(ospSkinModel, param)
                    } else {
                        XposedBridge.log("Skin is not active.")
                    }
                }

                private fun exportSkins() {
                    val alipaySkinsRootFile = File("$PACKAGE_PATH$ALIPAY_SKINS_ROOT")
                    val fixedPathUpdatesFile = File(FIXED_PATH_UPDATES)
                    if (!alipaySkinsRootFile.exists()) {
                        XposedBridge.log("No skins found, ignore export")
                        return
                    }
                    fixedPathUpdatesFile.mkdirs()

                    alipaySkinsRootFile.listFiles().orEmpty()
                        .filter { it.isDirectory && it.name != HOHO_SKIN_SUB_FOLDER }
                        .forEach { skinFile ->
                            XposedBridge.log("Exporting skin: ${skinFile.name}")
                            skinFile.copyRecursively(
                                File("$FIXED_PATH_UPDATES/${skinFile.name}"), true
                            )
                        }
                }

                private fun File.manageSkinUpdateAndDelete() {
                    manageAction("$FIXED_PATH_UPDATES/delete") {
                        deleteRecursively()
                        XposedBridge.log("Trigger skin delete, skin deleted.")
                    }
                    manageAction("$FIXED_PATH_UPDATES/update") {
                        mkdirs()
                        File(FIXED_PATH_UPDATES).copyRecursively(this, true)
                        XposedBridge.log("Trigger skin update, skin updated.")
                    }
                }

                private fun updateSkins(
                    ospSkinModel: Class<*>, param: MethodHookParam
                ) {
                    val subFolder = searchFolders().randomOrNull() ?: ""
                    val skinModelJson = """
                        {"md5":"HOHO_MD5","minWalletVersion":"10.2.23.0000","outDirName":"HOHO/$subFolder","skinId":"HOHO_CUSTOMIZED","skinStyleId":"2022 New Year Happy!","userId":"HOHO"}
                    """.trimIndent()
                    val skinModel = JSON.parseObject(skinModelJson, ospSkinModel)
                    param.result = skinModel
                    XposedBridge.log("Skin updated.")
                }

                private fun searchFolders(): List<String> {
                    return File(FULL_SKIN_PATH).listFiles()?.filter {
                        it.isDirectory && it.name !in listOf(
                            "update", "actived", "delete"
                        )
                    }?.map { it.name }.orEmpty()
                }
            })
    }

    private fun File.checkAndDelete(action: () -> Unit) {
        if (exists()) {
            action()
            delete()
            XposedBridge.log("Export sign file deleted.")
        }
    }

    private fun File.manageAction(filePath: String, action: File.() -> Unit) {
        val file = File(filePath)
        if (file.exists()) {
            action()
            file.delete()
            XposedBridge.log("Action sign file deleted.")
        }
    }
}
