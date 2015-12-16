/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package com.goftagram.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.goftagram.telegram.messenger.AndroidUtilities;
import com.goftagram.telegram.messenger.ContactsController;
import com.goftagram.telegram.messenger.MessagesController;
import com.goftagram.telegram.messenger.R;
import com.goftagram.telegram.tgnet.TLRPC;
import com.goftagram.telegram.ui.Components.AvatarDrawable;
import com.goftagram.telegram.ui.Components.BackupImageView;
import com.goftagram.telegram.ui.Components.CheckBox;
import com.goftagram.telegram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {

    private BackupImageView imageView;
    private TextView nameTextView;
    private CheckBox checkBox;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();

    public ShareDialogCell(Context context) {
        super(context);

        imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(27));
        addView(imageView, LayoutHelper.createFrame(54, 54, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 7, 0, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextColor(0xff212121);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        nameTextView.setMaxLines(2);
        nameTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        nameTextView.setLines(2);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(nameTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 6, 64, 6, 0));

        checkBox = new CheckBox(context, R.drawable.round_check2);
        checkBox.setSize(24);
        checkBox.setCheckOffset(AndroidUtilities.dp(1));
        checkBox.setVisibility(VISIBLE);
        checkBox.setColor(0xff3ec1f9);
        addView(checkBox, LayoutHelper.createFrame(24, 24, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 17, 39, 0, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100), MeasureSpec.EXACTLY));
    }

    public void setDialog(TLRPC.Dialog dialog, boolean checked, CharSequence name) {
        int lower_id = (int) dialog.id;
        TLRPC.FileLocation photo = null;
        if (lower_id > 0) {
            TLRPC.User user = MessagesController.getInstance().getUser(lower_id);
            if (name != null) {
                nameTextView.setText(name);
            } else if (user != null) {
                nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                nameTextView.setText("");
            }
            avatarDrawable.setInfo(user);
            if (user != null && user.photo != null) {
                photo = user.photo.photo_small;
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance().getChat(-lower_id);
            if (name != null) {
                nameTextView.setText(name);
            } else if (chat != null) {
                nameTextView.setText(chat.title);
            } else {
                nameTextView.setText("");
            }
            avatarDrawable.setInfo(chat);
            if (chat != null && chat.photo != null) {
                photo = chat.photo.photo_small;
            }
        }
        imageView.setImage(photo, "50_50", avatarDrawable);
        checkBox.setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animated) {
        checkBox.setChecked(checked, animated);
    }
}
