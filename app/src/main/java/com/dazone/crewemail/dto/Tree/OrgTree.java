package com.dazone.crewemail.dto.Tree;


import com.dazone.crewemail.data.PersonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrgTree {

    public static PersonData buildTree(List<PersonData> dtos) throws Exception {
        PersonData root = new PersonData();
        HashMap<Integer, ArrayList<PersonData>> jsonDataMap = new HashMap<>();
        for (PersonData dto : dtos) {
            if (jsonDataMap.containsKey(dto.getDepartmentParentNo())) {
                if (dto.getType() == 2) {
                    ArrayList<PersonData> currentList = jsonDataMap.get(dto.getDepartmentParentNo());
                    currentList.add(dto);
                } else {
                    jsonDataMap.get(dto.getDepartmentParentNo()).add(dto);
                }
            } else {
                ArrayList<PersonData> subordinates = new ArrayList<>();
                subordinates.add(dto);
                jsonDataMap.put(dto.getDepartmentParentNo(), subordinates);
            }
        }
        return root;
    }
}
