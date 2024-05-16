/*
 * PacificSMP Plugin © 2024 by raffel080108 is licensed under CC BY-NC-SA 4.0. To view a copy of this license, visit https://creativecommons.org/licenses/by-nc-sa/4.0/
 */

package raffel080108.pacificsmp.data;

public enum DolphinType {
    DARK(true),
    SPEED(true),
    FLAME(true),
    POWER(true),
    HEALTHY(false);

    private final boolean retriggerOnRespawn;

    DolphinType(boolean retriggerOnRespawn) {
        this.retriggerOnRespawn = retriggerOnRespawn;
    }

    public boolean doesRetriggerOnRespawn() {
        return retriggerOnRespawn;
    }
}
