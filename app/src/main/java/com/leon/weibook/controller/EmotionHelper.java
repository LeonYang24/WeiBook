package com.leon.weibook.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;

import com.leon.weibook.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表情助手类
 * 用于
 * Created by Leon on 2016/5/16 0016.
 */
public class EmotionHelper {

	private static final int ONE_PAGE_SIZE = 21;
	public static List<List<String>> emojiGroups;
	private static Pattern pattern;
	private static String[] emojiCodes = new String[]{
			":smile:",
			":laughing:",
			":blush:",
			":smiley:",
			":relaxed:",
			":smirk:",
			":heart_eyes:",
			":kissing_heart:",
			":kissing_closed_eyes:",
			":flushed:",
			":relieved:",
			":satisfied:",
			":grin:",
			":wink:",
			":stuck_out_tongue_winking_eye:",
			":stuck_out_tongue_closed_eyes:",
			":grinning:",
			":kissing:",
			":kissing_smiling_eyes:",
			":stuck_out_tongue:",
			":sleeping:",
			":worried:",
			":frowning:",
			":anguished:",
			":open_mouth:",
			":grimacing:",
			":confused:",
			":hushed:",
			":expressionless:",
			":unamused:",
			":sweat_smile:",
			":sweat:",
			":disappointed_relieved:",
			":weary:",
			":pensive:",
			":disappointed:",
			":confounded:",
			":fearful:",
			":cold_sweat:",
			":persevere:",
			":cry:",
			":sob:",
			":joy:",
			":astonished:",
			":scream:",
			":tired_face:",
			":angry:",
			":rage:",
			":triumph:",
			":sleepy:",
			":yum:",
			":mask:",
			":sunglasses:",
			":dizzy_face:",
			":neutral_face:",
			":no_mouth:",
			":innocent:",
			":thumbsup:",
			":thumbsdown:",
			":clap:",
			":point_right:",
			":point_left:"};

	static {
		//算出表情所占页数
		int pages = emojiCodes.length / ONE_PAGE_SIZE +
				    (emojiCodes.length % ONE_PAGE_SIZE == 0 ? 0 : 1);
		emojiGroups = new ArrayList<>();
		for (int page = 0; page < pages; page++) {
			List<String> onePageEmojis = new ArrayList<>();
			int start = page * ONE_PAGE_SIZE;
			int end = Math.min(page * ONE_PAGE_SIZE + ONE_PAGE_SIZE, emojiCodes.length);
			for (int i = start; i < end; i++) {
				onePageEmojis.add(emojiCodes[i]);
			}
			emojiGroups.add(onePageEmojis);//添加一页
		}
		pattern = Pattern.compile("\\:[a-z0-9-_]*\\:");//编译后的正则表达式模式
	}

	/**
	 * 判断strings中是否包含string
	 * @param strings
	 * @param string
	 * @return
	 */
	public static boolean contain(String[] strings, String string) {
		for (String s : strings) {
			if (s.equals(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 替换所传入的文本中的图片信息，并让字符串和图片共存于EditText中。
	 * @param context
	 * @param text
	 * @return
	 */
	public static CharSequence replace(Context context, String text) {
		if (TextUtils.isEmpty(text)) {
			return text;
		}
		SpannableString spannableString = new SpannableString(text);
		Matcher matcher = pattern.matcher(text);//匹配文本是否符合模式规则，返回一个matcher
		while (matcher.find()) {
			String factText = matcher.group();//返回当前查找而获得的与组匹配的所有子串内容
			String key = factText.substring(1, factText.length() - 1);//不要首尾的“:”
			if (contain(emojiCodes, factText)) {
				Bitmap bitmap = getEmojiDrawable(context, key);
				ImageSpan image = new ImageSpan(context, bitmap);
				int start = matcher.start();//返回当前查找所获子串的开始字符在原目标字符串中的位置
				int end = matcher.end();//返回当前查找所获子串的结束字符在原目标字符串中的位置
				spannableString.setSpan(image, start, end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		return spannableString;
	}

	public static void isEmojiDrawableAvailable(Context context) {
		for (String emojiCode : emojiCodes) {
			String code = emojiCode.substring(1, emojiCode.length() - 1);
			Bitmap bitmap = getDrawableByName(context, code);
			if (bitmap == null) {
				LogUtils.i("not available test " + code);
			}
		}
	}

	/**
	 * 获取表情图片 （通过调用getDrawableByName）
	 * @param context
	 * @param name
	 * @return
	 */
	public static Bitmap getEmojiDrawable(Context context, String name) {
		return getDrawableByName(context, "emoji_" + name);
	}

	/**
	 * 通过名字获取表情图片资源
	 * @param ctx
	 * @param name
	 * @return
	 */
	public static Bitmap getDrawableByName(Context ctx, String name) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap bitmap = BitmapFactory.decodeResource(ctx.getResources(),
				ctx.getResources().getIdentifier(name, "drawable",
						ctx.getPackageName()), options);
		return bitmap;
	}

}
