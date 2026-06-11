package com.yueting.common.service.impl;

import com.yueting.common.mapper.SysDictMapper;
import com.yueting.common.service.SysDictService;
import com.yueting.entity.dto.SysDictSaveDTO;
import com.yueting.entity.po.SysDict;
import com.yueting.entity.vo.SysDictVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDictServiceImpl implements SysDictService {
    private final SysDictMapper sysDictMapper;

    public SysDictServiceImpl(SysDictMapper sysDictMapper) {
        this.sysDictMapper = sysDictMapper;
    }

    @Override
    public List<SysDictVO> listTypes() {
        List<SysDict> all = sysDictMapper.selectList(null);
        List<SysDictVO> result = new ArrayList<>();
        for (SysDict d : all) {
            if (d.getParentId() == null || d.getParentId() == 0) {
                SysDictVO vo = toVO(d);
                vo.setChildren(buildChildren(d.getId()));
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public List<SysDictVO> listByCode(String dictCode) {
        SysDict type = sysDictMapper.selectByCode(dictCode);
        if (type == null) return new ArrayList<>();
        return buildChildren(type.getId());
    }

    @Override
    public SysDictVO getById(Long id) {
        SysDict d = sysDictMapper.selectById(id);
        return d != null ? toVO(d) : null;
    }

    @Override
    @Transactional
    public void save(SysDictSaveDTO dto) {
        SysDict dict = new SysDict();
        BeanUtils.copyProperties(dto, dict);
        sysDictMapper.insert(dict);
    }

    @Override
    @Transactional
    public void update(SysDictSaveDTO dto) {
        SysDict dict = new SysDict();
        BeanUtils.copyProperties(dto, dict);
        sysDictMapper.update(dict);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        int cnt = sysDictMapper.countByParentId(id);
        if (cnt > 0) {
            throw new RuntimeException("该字典类型下存在" + cnt + "条数据，请先删除子数据");
        }
        sysDictMapper.deleteById(id);
    }

    private List<SysDictVO> buildChildren(Long pid) {
        return sysDictMapper.selectByParentId(pid).stream().map(this::toVO).collect(Collectors.toList());
    }

    private SysDictVO toVO(SysDict d) {
        SysDictVO vo = new SysDictVO();
        BeanUtils.copyProperties(d, vo);
        return vo;
    }
}