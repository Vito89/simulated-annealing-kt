## Исходный код алгоритма "Отжиг"
import random
import math

class AnnealingProblem:
    def __init__(self, start_state, energy_func, neighbor_func):
        self.start_state = start_state
        self.energy_func = energy_func
        self.neighbor_func = neighbor_func

    def anneal(self, max_iter, temp_start, temp_end):
        temp = temp_start
        state = self.start_state
        best_state = self.start_state
        best_energy = self.energy_func(self.start_state)

        for i in range(max_iter):
            new_state = self.neighbor_func(state)
            new_energy = self.energy_func(new_state)
            delta_energy = new_energy - self.energy_func(state)

            if delta_energy < 0 or random.uniform(0, 1) < math.exp(-delta_energy / temp):
                state = new_state

            if self.energy_func(state) < best_energy:
                best_state = state
                best_energy = self.energy_func(state)

            temp *= 0.99

        return best_state, best_energy
```