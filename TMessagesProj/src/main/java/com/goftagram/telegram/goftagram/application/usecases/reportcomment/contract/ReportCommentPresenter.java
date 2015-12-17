package com.goftagram.telegram.goftagram.application.usecases.reportcomment.contract;

import com.goftagram.telegram.goftagram.application.usecases.absclasses.abspost.AbsPostPresenter;
import com.goftagram.telegram.goftagram.application.usecases.getchannelmetadata.contract.GetChannelMetaDataViewModel;

/**
 * Created by WORK on 10/30/2015.
 */
public interface ReportCommentPresenter extends AbsPostPresenter<GetChannelMetaDataViewModel> {

    String KEY_REPORT_TEXT = "Report_Text";
    String KEY_COMMENT_ID  = "Comment_Id";
}
