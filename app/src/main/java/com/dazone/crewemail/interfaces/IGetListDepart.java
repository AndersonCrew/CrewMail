package com.dazone.crewemail.interfaces;

import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.dto.Tree.Dtos.TreeUserDTO;

import java.util.ArrayList;

/**
 * Created by Dazone on 7/6/2017.
 */

public interface IGetListDepart {
    void onGetListDepartSuccess(ArrayList<TreeUserDTO> treeUserDTOs);

    void onGetListDepartFail(ErrorData dto);
}