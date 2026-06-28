package src.main.model.entity.spell;

import src.main.model.entity.enemy.Enemy;
import src.main.model.entity.knight.Knight;
import src.main.model.entity.charm.CharmType;
import src.main.model.enviroment.SolidBlock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class SpellManager {
    private final Knight knight;
    private final List<Enemy> enemies;
    private final List<SolidBlock> solidBlocks;
    private final List<VengefulProjectile> projectiles = new ArrayList<>();
    private final List<HowlingWraithsAoe> aoes = new ArrayList<>();
    private Consumer<Enemy> onKill;

    public SpellManager(Knight knight, List<Enemy> enemies, List<SolidBlock> solidBlocks) {
        this.knight = knight;
        this.enemies = enemies;
        this.solidBlocks = solidBlocks;
    }

    public void setOnKill(Consumer<Enemy> onKill) { this.onKill = onKill; }

    public void firePending(SpellType type) {
        if (type == null) return;
        switch (type) {
            case VENGEFUL -> fireVengefulSpirit();
            case WRAITHS -> fireHowlingWraiths();
        }
    }

    private void fireVengefulSpirit() {
        float x = knight.isFacingRight()
            ? knight.getBoundingBox().x + knight.getBoundingBox().width
            : knight.getBoundingBox().x - 16;
        float y = knight.getPosition().y + 20;
        boolean shadow = knight.isCharmEquipped(CharmType.VOID_HEART);
        projectiles.add(new VengefulProjectile(x, y, knight.isFacingRight(), shadow));
    }

    private void fireHowlingWraiths() {
        boolean shadow = knight.isCharmEquipped(CharmType.VOID_HEART);
        aoes.add(new HowlingWraithsAoe(
            knight.getBoundingBox().x, knight.getBoundingBox().y,
            knight.getBoundingBox().width, knight.getBoundingBox().height, shadow));
    }

    public void updateProjectiles(float delta) {
        Iterator<VengefulProjectile> it = projectiles.iterator();
        while (it.hasNext()) {
            VengefulProjectile p = it.next();
            if (p.isDead()) { it.remove(); continue; }
            if (p.checkWallCollision(delta, solidBlocks)) {
                it.remove();
                continue;
            }
            p.update(delta);
            for (Enemy enemy : enemies) {
                if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;
                if (p.getBoundingBox().overlaps(enemy.getBoundingBox()) && p.tryHit(enemy)) {
                    enemy.takeDamage(knight.getSpellDamage());
                    knight.addSoul(knight.getSoulPerHit());
                    if (enemy.isDead() && onKill != null) onKill.accept(enemy);
                }
            }
        }
    }

    public void updateAoes(float delta) {
        Iterator<HowlingWraithsAoe> it = aoes.iterator();
        while (it.hasNext()) {
            HowlingWraithsAoe aoe = it.next();
            if (aoe.isDone()) { it.remove(); continue; }
            int prevTick = aoe.getTickCount();
            aoe.update(delta);
            if (aoe.getTickCount() > prevTick) {
                for (Enemy enemy : enemies) {
                    if (enemy.isDead() || enemy.isDeadAnimationDone()) continue;
                    if (aoe.getBounds().overlaps(enemy.getBoundingBox())) {
                        enemy.takeDamage(knight.getSpellDamage());
                        knight.addSoul(knight.getSoulPerHit());
                        if (enemy.isDead() && onKill != null) onKill.accept(enemy);
                    }
                }
            }
        }
    }

    public List<VengefulProjectile> getProjectiles() { return projectiles; }
    public List<HowlingWraithsAoe> getAoes() { return aoes; }
}
