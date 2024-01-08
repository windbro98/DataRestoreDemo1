package com.ui;

import javafx.scene.paint.Color;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

import java.io.File;
import java.time.format.DateTimeFormatter;

import static com.util.FileToolUtil.fileConcat;

public class UIConstants {
    private UIConstants(){};

    final public static int WIDTH = 900; // UI界面宽度
    final public static int HEIGHT = 650; // UI界面高度
    final public static double BUTTON_WIDTH_RATIO = 0.1; // 按钮宽度
    final public static double PROMPT_WIDTH_RATIO = 0.15; // 提示词宽度
    final public static double CONTENT_WIDTH_RATIO = 0.5; // 输入内容宽度
    final public static double INNER_PROMPT_WIDTH_RATIO = 0.1; // 输入内容中的提示词宽度
    final public static double INNER_UNIT_WIDTH_RATIO = 0.05; // 输入内容中的单位宽度
    final public static double TIME_WIDTH_RATIO = 0.15; // 时间宽度
    final public static double SIZE_WIDTH_RATIO = 0.1; // 文件大小宽度
    final public static double PADDING_RATIO = 0.03; // 填充宽度
    final public static double SPACE_RATIO = 0.05; // 按钮间空白宽度
    final public static Color HOVER_COLOR = Color.color(0.7294118, 0.7294118, 0.7294118); // 左侧菜单框颜色
    final public static double MENU_WIDTH_RATIO = 0.2; // 菜单框宽度
    final public static int MENU_FONT_SIZE = 18; // 菜单字体大小
    final public static int CONTENT_FONT_SIZE = 16; // 内容字体大小
    final public static double IMAGE_WIDTH_RATIO = 0.1; // 图片宽度
    final public static Material2MZ BUTTON_FILE_ICON = Material2MZ.PAGEVIEW; // 文件选择按钮图标
    final public static Material2OutlinedAL POPUP_CHECK_ICON = Material2OutlinedAL.CHECK_CIRCLE;
    final public static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // 时间格式
    final public static String PROJECT_DIR = System.getProperty("user.dir");
}
