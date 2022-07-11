package com.dazone.crewemail.adapter;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.activities.ActivityMailCreate;
import com.dazone.crewemail.activities.ActivityMailDetail;
import com.dazone.crewemail.activities.ReadDateActivity;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.data.MailData;
import com.dazone.crewemail.data.MailTagMenuData;
import com.dazone.crewemail.data.PersonData;
import com.dazone.crewemail.fragments.ListEmailFragment;
import com.dazone.crewemail.fragments.SearchFragment;
import com.dazone.crewemail.interfaces.BaseHTTPCallBack;
import com.dazone.crewemail.interfaces.OnMenuListCallBack;
import com.dazone.crewemail.utils.EmailBoxStatics;
import com.dazone.crewemail.utils.MailHelper;
import com.dazone.crewemail.utils.PreferenceUtilities;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.StaticsBundle;
import com.dazone.crewemail.utils.TimeUtils;
import com.dazone.crewemail.utils.Util;
import com.dazone.crewemail.webservices.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sherry on 12/17/15.
 */
public class ListEmailRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements BaseHTTPCallBack {

    private static final int FOOTER = 0;
    private static final int NORMAL = 1;
    private final String mRootLink;
    private final String mMailBoxClassName;
    private ArrayList<MailData> mMailDataList;
    private boolean mIsProgressOn;
    private Activity mContext;
    private Fragment mFragment;
    private OnMailItemSelectListener mOnMailItemSelectListener;
    private ArrayList<MailData> mSelectedMailList = new ArrayList<>();


    public ListEmailRecyclerAdapter(Fragment fragment, ArrayList<MailData> mailDataList, String mailBoxClassName) {
        mMailDataList = mailDataList;
        this.mContext = fragment.getActivity();
        this.mFragment = fragment;
        this.mMailBoxClassName = mailBoxClassName;
        mRootLink = DaZoneApplication.getInstance().getPrefs().getServerSite();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_mail, parent, false);
            return new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position >= getItemCount() - 1) {
            final ProgressViewHolder v = (ProgressViewHolder) holder;
            if (mIsProgressOn) {
                v.progressBar.setVisibility(View.VISIBLE);
            } else {
                v.progressBar.setVisibility(View.GONE);
            }
        } else {
            final ItemViewHolder vh = (ItemViewHolder) holder;
            final MailData mailData = mMailDataList.get(position);
            if (mailData.getMailNo() < 0) {
                vh.relMainList.setVisibility(View.GONE);
                vh.linearMainMonth.setVisibility(View.VISIBLE);
                if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                    vh.txtMonth.setText(TimeUtils.GetMonth(mailData.getMailNo(), TimeUtils.getTimeWithTimezone(mailData.getRegisterDate())));
                } else {
                    vh.txtMonth.setText(TimeUtils.GetMonth(mailData.getMailNo(), mailData.getRegisterDate()));
                }
            } else {
                if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                    vh.txtMonth.setText(TimeUtils.GetMonth(mailData.getMailNo(), TimeUtils.getTimeWithTimezone(mailData.getRegisterDate())));
                } else {
                    vh.txtMonth.setText(TimeUtils.GetMonth(mailData.getMailNo(), mailData.getRegisterDate()));

                }
                vh.relMainList.setVisibility(View.VISIBLE);
                vh.linearMainMonth.setVisibility(View.GONE);
                vh.mainWrapper.setTag(mailData);
                vh.mainWrapper.setOnClickListener(v -> runAnimationStateForMail(vh, mailData, 2));
                vh.mainWrapper.setOnLongClickListener(v -> {
                    runAnimationStateForMail(vh, mailData, 1);
                    return true;
                });

                // set selected state for wrapper
                vh.mainWrapper.setActivated(mailData.isSelected());
                if (mailData.isSelected()) {
                    vh.avatarCheck.setAlpha(1.0f);
                    vh.avatarCheck.setRotationY(0);

                } else {
                    vh.avatarCheck.setAlpha(0.0f);
                }
                //set name
                if (!mailData.isRead()) {
                    vh.name.setTypeface(null, Typeface.BOLD);
                    vh.title.setTypeface(null, Typeface.BOLD);
                    vh.mainWrapper.setBackgroundResource(R.color.mail_unread);
                } else {
                    vh.name.setTypeface(null, Typeface.NORMAL);
                    vh.title.setTypeface(null, Typeface.NORMAL);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        vh.mainWrapper.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_press_gray));
                    } else {
                        vh.mainWrapper.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.btn_press_gray));
                    }
                }
                if (mailData.isAttachFile()) {
                    vh.attachment.setVisibility(View.VISIBLE);
                } else {
                    vh.attachment.setVisibility(View.GONE);
                }

                if (mailData.getmImageNo() != 0) {
                    vh.tag.setImageResource(MailHelper.getColorTag(mailData.getmImageNo()));
                    vh.tag.setVisibility(View.VISIBLE);
                } else {
                    vh.tag.setVisibility(View.GONE);
                }

                vh.tag.setOnClickListener(v -> GetTagMenu(vh, mailData.getMailNo(), false));

                // set important mail
                if (mailData.isImportant()) {
                    vh.favorite.setImageResource(R.drawable.list_star_yelow_ic);
                    vh.favorite.setVisibility(View.VISIBLE);
                } else {
                    vh.favorite.setVisibility(View.GONE);
                }
                vh.favorite.setOnClickListener(v -> {
                    if (mFragment instanceof ListEmailFragment) {
                        ((ListEmailFragment) mFragment).setEmailAsImportant(mailData);
                    }
                    if (mFragment instanceof SearchFragment) {
                        ((SearchFragment) mFragment).setEmailAsImportant(mailData);
                    }
                });
                String displayName = "";
                if (Locale.getDefault().getLanguage().toUpperCase().equalsIgnoreCase("KO"))
                    if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                        vh.date.setText(TimeUtils.displayTimeWithoutOffset(mContext, TimeUtils.getTimeWithTimezone(mailData.getRegisterDate()), 1));
                    } else {
                        vh.date.setText(TimeUtils.displayTimeWithoutOffset(mContext, mailData.getRegisterDate(), 1));
                    }

                else {
                    if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                        vh.date.setText(TimeUtils.displayTimeWithoutOffset(mContext, TimeUtils.getTimeWithTimezone(mailData.getRegisterDate()), 0));
                    } else {
                        vh.date.setText(TimeUtils.displayTimeWithoutOffset(mContext, mailData.getRegisterDate(), 0));
                    }


                }
                if (!mailData.isSend()) {
                    //set information to view
                    vh.mainReadDate.setVisibility(View.GONE);
                    displayName = (!TextUtils.isEmpty(mailData.getFromName())) ? mailData.getFromName() : mailData.getFromEmail();
                    // set avatar
                    if (TextUtils.isEmpty(mailData.getAvatar())) {
                        int iconColor = mailData.getColor();
                        if (iconColor < 1) {
                            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                            // generate color based on a key (same key returns the same color), useful for list/grid views
                            iconColor = generator.getColor(mailData.getFromEmail() + mailData.getFromEmail());
                            // set to object
                            mailData.setColor(iconColor);
                        }
                        TextDrawable drawable;
                        if (mailData.getFirstLetterOfName().equalsIgnoreCase("\""))
                            drawable = TextDrawable.builder()
                                    .buildRound(mailData.getSecondLetterOfName(), iconColor);
                        else
                            drawable = TextDrawable.builder()
                                    .buildRound(mailData.getFirstLetterOfName(), iconColor);
                        drawable.setPadding(1, 1, 1, 1);

                        vh.avatar.setImageDrawable(drawable);

                    } else {
                        String url = mRootLink + mailData.getAvatar();
                        ImageLoader.getInstance().displayImage(url, vh.avatar, Statics.options2);
                    }
                } else {
                    vh.name.setMaxWidth((int) mContext.getResources().getDimension(R.dimen.dimen_110_220));
                    vh.mainReadDate.setVisibility(View.VISIBLE);
                    vh.mainReadDate.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, ReadDateActivity.class);
                        intent.putExtra(StaticsBundle.BUNDLE_MAIL_NO_NEW, mailData.getMailNo());
                        intent.putExtra(StaticsBundle.BUNDLE_TITLE, mailData.getTitle());
                        intent.putExtra(StaticsBundle.BUNDLE_TIME, mailData.getRegisterDate());
                        mContext.startActivity(intent);
                    });

                    String count = mailData.getmIsReadCount() + "/" + mailData.getmRecipientCount();
                    vh.readDate.setText(count);
                    if (mailData.getListPersonDataAll() != null && mailData.getListPersonDataAll().size() > 0) {
                        PersonData personData = mailData.getListPersonDataAll().get(0);
                        if (TextUtils.isEmpty(personData.getFullName())) {
                            int index = personData.getEmail().indexOf('@');
                            if (index != -1) {
                                displayName = personData.getEmail().substring(0, index);
                            } else {
                                displayName = personData.getEmail();
                            }
                        } else {
                            displayName += personData.getFullName();
                        }
                    }

                    // set avatar
                    if (mailData.getAvatarTo() == null || mailData.getAvatarTo().size() == 0) {
                        int iconColor = mailData.getColor();
                        if (iconColor < 1) {
                            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                            // generate color based on a key (same key returns the same color), useful for list/grid views
                            iconColor = generator.getColor(displayName + displayName);
                            // set to object
                            mailData.setColor(iconColor);
                        }
                        TextDrawable drawable;
                        if (mailData.getFirstLetterOfName().equalsIgnoreCase("\""))
                            drawable = TextDrawable.builder()
                                    .buildRound(mailData.getSecondLetterOfName(displayName), iconColor);
                        else
                            drawable = TextDrawable.builder()
                                    .buildRound(mailData.getFirstLetterOfName(displayName), iconColor);
                        drawable.setPadding(1, 1, 1, 1);
                        vh.avatar.setImageDrawable(drawable);
                    } else {
                        if (!TextUtils.isEmpty(mailData.getAvatarTo().get(0))) {
                            String url = mRootLink + mailData.getAvatarTo().get(0);
                            ImageLoader.getInstance().displayImage(url, vh.avatar, Statics.options2);
                        } else {
                            int iconColor = mailData.getColor();
                            if (iconColor < 1) {
                                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                                // generate color based on a key (same key returns the same color), useful for list/grid views
                                iconColor = generator.getColor(displayName + displayName);
                                // set to object
                                mailData.setColor(iconColor);
                            }
                            TextDrawable drawable;
                            if (mailData.getFirstLetterOfName().equalsIgnoreCase("\""))
                                drawable = TextDrawable.builder()
                                        .buildRound(mailData.getSecondLetterOfName(displayName), iconColor);
                            else
                                drawable = TextDrawable.builder()
                                        .buildRound(mailData.getFirstLetterOfName(displayName), iconColor);
                            drawable.setPadding(1, 1, 1, 1);
                            vh.avatar.setImageDrawable(drawable);
                        }
                    }

                }
                if (TextUtils.isEmpty(displayName)) {
                    vh.name.setText(Util.getString(R.string.string_not_name));
                } else {
                    vh.name.setText(displayName.trim());
                }
                String title = mailData.getTitle().trim();
                if (TextUtils.isEmpty(title)) {
                    vh.title.setText(Util.getString(R.string.string_not_subjet));
                } else {
                    vh.title.setText(title);
                }
                vh.content.setText(mailData.getContentWithoutHtml().trim());
                if (mailData.isDeleted()) {
                    vh.mainWrapper.setEnabled(false);
                    vh.favorite.setClickable(false);
                    vh.avatarCheck.setClickable(false);
                    vh.status.setVisibility(View.VISIBLE);
                    vh.status.setText(mailData.getDisplayDeleteString());
                } else {
                    vh.mainWrapper.setEnabled(true);
                    vh.status.setVisibility(View.GONE);
                    vh.status.setText("");
                }
            }
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    private boolean isOnTouch = false;

    public void runAnimationStateForMail(ItemViewHolder vh, MailData mailData, int isTask) {
        if (isTask == 1) {
            isOnTouch = true;
            if (mOnMailItemSelectListener != null) {
                mOnMailItemSelectListener.onMailItemSelect(null);
            }
        } else {
            if (isOnTouch) {
                if (!mailData.isSelected()) {
                    showHideCheckAnimation(vh, true);
                    mailData.setSelected(true);
                } else {
                    showHideCheckAnimation(vh, false);
                    mailData.setSelected(false);
                }
                vh.mainWrapper.setActivated(mailData.isSelected());
                if (mOnMailItemSelectListener != null) {
                    mOnMailItemSelectListener.onMailItemSelect(mailData);
                }
                AddOrRemoveMail(mailData);
            } else {
                if (mMailBoxClassName.equalsIgnoreCase(EmailBoxStatics.MAIL_CLASS_DRAFT_BOX)) {
                    Intent i = new Intent(mContext, ActivityMailCreate.class);
                    i.putExtra(StaticsBundle.BUNDLE_MAIL_NO, mailData.getMailNo());
                    i.putExtra(StaticsBundle.BUNDLE_MAIL_DETAIL_TASK, 3);
                    mFragment.startActivityForResult(i, Statics.EMAIL_DETAIL_ACTIVITY);
                } else {
                    Intent i = new Intent(mContext, ActivityMailDetail.class);
                    i.putExtra(StaticsBundle.BUNDLE_MAIL_NO, mailData.getMailNo());
                    i.putExtra(StaticsBundle.BUNDLE_MAIL_DATA, mailData);
                    i.putExtra(StaticsBundle.BUNDLE_MAIL_BOX_CLASS_NAME, mMailBoxClassName);
                    i.putExtra(StaticsBundle.PREFS_KEY_ISREAD, mailData.isRead());
                    mFragment.startActivityForResult(i, Statics.EMAIL_DETAIL_ACTIVITY);
                }
            }

        }
    }

    private void AddOrRemoveMail(MailData mailData) {
        if (mSelectedMailList.contains(mailData)) {
            // remove if already add selected list
            mSelectedMailList.remove(mailData);
        } else {
            mSelectedMailList.add(mailData);
        }
    }

    public void showHideCheckAnimation(ItemViewHolder vh, boolean isShowCheck) {
        if (isShowCheck) {
            final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_right);

            final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_left_in);
            setRightOut.setTarget(vh.avatar);
            setRightOut.start();
            setLeftIn.setTarget(vh.avatarCheck);
            setLeftIn.start();
        } else {
            final AnimatorSet setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_right_out);

            final AnimatorSet setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(mContext,
                    R.animator.flip_left_in);
            setRightOut.setTarget(vh.avatarCheck);
            setRightOut.start();
            setLeftIn.setTarget(vh.avatar);
            setLeftIn.start();
        }
        vh.mainWrapper.setActivated(isShowCheck);
    }

    public void setOnMailItemSelectListener(OnMailItemSelectListener listener) {
        this.mOnMailItemSelectListener = listener;
    }

    @Override
    public int getItemCount() {
        return mMailDataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= getItemCount() - 1) {
            return FOOTER;
        } else {
            return NORMAL;
        }
    }

    public void setProgressOn(boolean isOn) {
        this.mIsProgressOn = isOn;
        this.notifyItemChanged(getItemCount() - 1);
    }

    public void ClearListSelect() {
        if (mSelectedMailList != null) {
            mSelectedMailList.clear();
            mSelectedMailList = new ArrayList<>();
        }

        isOnTouch = false;
    }

    public void GetTagMenu(final ItemViewHolder itemViewHolder, final long mailNo, boolean isSkipCache) {
        final LinkedHashMap<String, Object> menuList = null;
        String cachedMenuJson = DaZoneApplication.getInstance().getPrefs().getMenuListData();
        String accessToken = DaZoneApplication.getInstance().getPrefs().getAccessToken();
        // get menu from cache if exist
        if (!isSkipCache && !TextUtils.isEmpty(cachedMenuJson) && cachedMenuJson.contains(accessToken + "#@#")) {
            String[] splitString = cachedMenuJson.split("#@#");
            ShowDialog(MailHelper.convertJsonStringToMap(splitString[1]), itemViewHolder, mailNo);
        } else {
            // don't have any menu in cache , request server for the new one
            HttpRequest.getInstance().getEmailMenuList(new OnMenuListCallBack() {
                @Override
                public void onMenuListSuccess(LinkedHashMap menuMap) {
                    ShowDialog(menuMap, itemViewHolder, mailNo);
                }

                @Override
                public void onMenuListFail(ErrorData errorData) {
                    ShowDialog(menuList, itemViewHolder, mailNo);
                }
            });
        }
    }

    public void ShowDialog(LinkedHashMap menuMap, final ItemViewHolder itemViewHolder, final long mailNo) {
        final List<MailTagMenuData> itemList = new ArrayList<>();
        if (menuMap != null) {
            Iterator<?> keySets = menuMap.keySet().iterator();
            while (keySets.hasNext()) {
                String key = (String) keySets.next();
                List<?> menuList = (List<?>) menuMap.get(key);
                for (Object menuData : menuList) {
                    if (menuData instanceof MailTagMenuData) {
                        itemList.add((MailTagMenuData) menuData);
                    }
                }
            }
        }
        itemList.add(0, new MailTagMenuData(0, 0, mContext.getString(R.string.string_title_mail_no_tag), false));
        MailHelper.displaySingleChoice(mContext, itemList, (dialog, which) -> {
            itemViewHolder.tag.setImageResource(MailHelper.getColorTag(itemList.get(which).getImageNo()));
            HttpRequest.getInstance().updateTagOfMail(itemList.get(which).getTagNo(), mailNo, ListEmailRecyclerAdapter.this);
        }, mContext.getString(R.string.app_name));
    }

    @Override
    public void onHTTPSuccess() {
        Util.showMessage(mContext.getString(R.string.string_success));
    }

    @Override
    public void onHTTPFail(ErrorData errorDto) {
    }

    public interface OnMailItemSelectListener {
        void onMailItemSelect(MailData mailData);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainWrapper;
        private LinearLayout mainReadDate;
        private RelativeLayout relMainList;
        private LinearLayout linearMainMonth;
        private ImageView avatar;
        private ImageView avatarCheck;
        private ImageView attachment;
        private ImageView tag;
        private ImageView favorite;
        private TextView readDate;
        private TextView name;
        private TextView status;
        private TextView date;
        private TextView title;
        private TextView content;
        private TextView txtMonth;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mainWrapper = itemView.findViewById(R.id.item_mail_wrapper);
            avatar = itemView.findViewById(R.id.item_mail_avatar);
            avatarCheck = itemView.findViewById(R.id.item_mail_avatar_check);
            attachment = itemView.findViewById(R.id.item_mail_attachment);
            tag = itemView.findViewById(R.id.item_mail_tag);
            favorite = itemView.findViewById(R.id.item_mail_star);
            name = itemView.findViewById(R.id.item_mail_name);
            status = itemView.findViewById(R.id.item_mail_status);
            title = itemView.findViewById(R.id.item_mail_title);
            date = itemView.findViewById(R.id.item_mail_date);
            content = itemView.findViewById(R.id.item_mail_content);
            txtMonth = itemView.findViewById(R.id.txtMonth);
            linearMainMonth = itemView.findViewById(R.id.linearMainMonth);
            relMainList = itemView.findViewById(R.id.relMailListMain);
            mainReadDate = itemView.findViewById(R.id.mainReadDate);
            readDate = itemView.findViewById(R.id.item_mail_txt_read_date);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
