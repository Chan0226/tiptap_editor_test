package com.example.editor.repository;

import com.example.editor.model.AttachmentVo;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AttachDao {
    public void insertAttach(AttachmentVo attachmentVo);
}
