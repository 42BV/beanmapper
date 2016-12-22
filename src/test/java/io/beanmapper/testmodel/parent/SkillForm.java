package io.beanmapper.testmodel.parent;

import io.beanmapper.annotations.BeanParent;

public class SkillForm {

    public String name;

    @BeanParent
    public PlayerForm player1;

    public PlayerForm player2;

}
