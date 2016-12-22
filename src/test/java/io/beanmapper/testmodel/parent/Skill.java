package io.beanmapper.testmodel.parent;

import io.beanmapper.annotations.BeanParent;

public class Skill {

    private String name;

    private Player player1;

    @BeanParent
    private Player player2;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }
}
