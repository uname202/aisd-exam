package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;
import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    /**
     * 1. Внешний цикл while выполняется O(n) раз в худшем случае;
     * 2. На каждой итерации:
     *   - Создание ArrayList и копирование элементов: O(n);
     *   - Сортировка O(n log n);
     *   - Проход по списку юнитов O(n);
     *   - Операция removeAll: O(n);
     * Итого: O(n * (n log n + n)) = O(n² * log n).
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> allUnits = new ArrayList<>();
        allUnits.addAll(playerArmy.getUnits());
        allUnits.addAll(computerArmy.getUnits());

        while (hasAliveUnits(playerArmy) && hasAliveUnits(computerArmy)) {
            List<Unit> currentUnits = new ArrayList<>(allUnits);
            currentUnits.sort((u1, u2) -> Integer.compare(u2.getBaseAttack(), u1.getBaseAttack()));

            List<Unit> toRemove = new ArrayList<>();
            for (Unit unit : currentUnits) {
                if (!unit.isAlive()) {
                    toRemove.add(unit);
                    continue;
                }

                Unit target = unit.getProgram().attack();
                if (target != null) {
                    printBattleLog.printBattleLog(unit, target);
                    if (!target.isAlive()) {
                        toRemove.add(target);
                    }
                }
            }

            allUnits.removeAll(toRemove);
        }
    }

    private boolean hasAliveUnits(Army army) {
        return army.getUnits().stream().anyMatch(Unit::isAlive);
    }
}