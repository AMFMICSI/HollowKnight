package src.main.model.entity.knight;

public class CheatSystem {
    private boolean noclipMode = false;
    private boolean godMode = false;

    public void toggleGodMode() { this.godMode = !this.godMode; }
    public boolean isGodMode() { return godMode; }

    public void toggleNoclip() { this.noclipMode = !this.noclipMode; }
    public boolean isNoclipMode() { return noclipMode; }

    public void emergencyHeal(Knight knight) {
        if (knight.isDead() || knight.getHp() <= 0) {
            knight.setHp(1);
            knight.respawn();
        } else {
            knight.setHp(Math.min(knight.getHp() + 1, knight.getMaxHp()));
        }
    }

    public void refillSoul(Knight knight) {
        knight.setSoul(knight.getMaxSoul());
    }
}
