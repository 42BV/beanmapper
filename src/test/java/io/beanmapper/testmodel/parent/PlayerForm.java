package io.beanmapper.testmodel.parent;

import java.util.List;

import io.beanmapper.annotations.BeanCollection;

public class PlayerForm {

    public String name;

    public SkillForm skill;

    @BeanCollection(elementType = Skill.class)
    public List<SkillForm> skills;

}
