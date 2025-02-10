package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GeneratePresetImpl implements GeneratePreset {

    /** Итоговая сложность:
     * T(n,m) = O(n) + O(n log n) + O(n × m/n)
     *        = O(n) + O(n log n) + O(m)
     *        = O(n log n + m), что меньше O(n * m)
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        List<Unit> sortedUnits = new ArrayList<>(unitList);
        sortedUnits.sort((u1, u2) -> {
            double eff1 = (double)(u1.getBaseAttack() + u1.getHealth()) / u1.getCost();
            double eff2 = (double)(u2.getBaseAttack() + u2.getHealth()) / u2.getCost();
            return Double.compare(eff2, eff1);
        });

        List<Unit> armyUnits = new ArrayList<>();
        int totalPoints = 0;
        int currentRow = 0;
        Map<String, Integer> unitCounter = new HashMap<>();

        for (Unit template : sortedUnits) {
            int unitCost = template.getCost();
            int availableUnits = Math.min(11, (maxPoints - totalPoints) / unitCost);

            for (int i = 0; i < availableUnits; i++) {
                if (totalPoints + unitCost > maxPoints) break;

                int x = 2 - (i % 3);
                int y = currentRow;

                int number = unitCounter.getOrDefault(template.getName(), 1);
                unitCounter.put(template.getName(), number + 1);

                armyUnits.add(new Unit(
                        template.getName() + " " + number,
                        template.getUnitType(),
                        template.getHealth(),
                        template.getBaseAttack(),
                        template.getCost(),
                        template.getAttackType(),
                        template.getAttackBonuses(),
                        template.getDefenceBonuses(),
                        x,
                        y
                ));

                totalPoints += unitCost;

                if (i > 0 && (i + 1) % 3 == 0) {
                    currentRow++;
                }
            }

            if (availableUnits > 0) {
                currentRow++;
            }
        }

        Army computerArmy = new Army();
        computerArmy.setUnits(armyUnits);
        computerArmy.setPoints(totalPoints);

        return computerArmy;
    }
}
