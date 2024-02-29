# Build Your Own World Design Document

**Partner 1:** Azalea Bailey

**Partner 2:** Ria Tao

## Description

This was a partner class project where we were tasked with building a pseudo-randomly generated world based on the user's seed input.
To play the game:
    1. run the Main.main() file
    2. press n
    3. type in a random seed (any sequence of numbers)
    4. press s (this takes you to the game)
Within the game, the player can use a, w, d, s to move the gardener avatar and j, i, l, k to move the duck avatar.
The gardener avatar is able to eat carrots. If the gardener avatar moves over a carrot, the gardener is taken to carrot world where they have the chance to eat the carrot. THey will be taken back to the original world after a set amount of time.
The duck avatar is not able to go to carrot world.

Additional Facts:
    1. If the player types in the same seed, the same pseudo-random world will appear.
    2. Despite being pseudo-random, the rooms will always be completely connected.
    3. If the player wishes to exit the game but play again later, the player can type ':Q'. If the player runs Main.main() and presses l (load game), the game will load from teh previously quit game with the same avatar position, duck position, and carrots eaten.

