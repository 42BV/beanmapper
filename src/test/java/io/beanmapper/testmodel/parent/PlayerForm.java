package io.beanmapper.testmodel.parent;

import io.beanmapper.annotations.BeanCollection;

import java.util.List;

public class PlayerForm {

    public String name;

    public SkillForm skill;

    @BeanCollection(elementType = Skill.class)
    public List<SkillForm> skills;

}
