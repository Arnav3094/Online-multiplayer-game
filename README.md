[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/IH-L4O39)
# Tic Tac Toe

Assignment A4 for the course CS F314

This is a starter code for the Tic Tac Toe multiplayer game app assignment.

It uses Android Navigation Component, with a single activity and three fragments:

- The DashboardFragment is the home screen. If a user is not logged in, it should navigate to the
  LoginFragment. (See the TODO comment in code.)

- The floating button in the dashboard creates a dialog that asks which type of game to create and
  passes that information to the GameFragment (using SafeArgs).

- The GameFragment UI has a 3x3 grid of buttons. They are initialized in the starter code.
  Appropriate listeners and game play logic needs to be provided.

- Pressing the back button in the GameFragment opens a dialog that confirms if the user wants to
  forfeit the game. (See the FIXME comment in code.)

- A "log out" action bar menu is shown on both the dashboard and the game fragments. Clicking it
  should log the user out and show the LoginFragment. This click is handled in the MainActivity.

## Developers
| Name                | BITS ID       | BITS Mail                       |
|---------------------|---------------|---------------------------------|
| Arnav Mangla        | 2022A7PS0244G | f20220244@goa.bits-pilani.ac.in |
| Prakhar Kumar Gupta | 2022A7PS1145G | f20221145@goa.bits-pilani.ac.in |

## Description
The Tic-Tac-Toe Game is a mobile app that offers an engaging two-player gameplay experience with Firebase, or gameplay in single-player mode. Players can play in single player mode, start a new two-player game and wait for another player to join or select from a list of open games to join. The app utilizes Firebase for real-time communication and updates, ensuring smooth gameplay interactions. With a focus on accessibility, the app is optimized for users with varying needs, offering scalable text sizes and support for screen readers.

## Known Bugs
No known bugs

## Implementation Summary
1. Firebase was set up and to make its use easier, a FirebaseManager class was created.
2. The log in functionality was implemented
3. The register functionality was implemented
4. The game fragment was first made keeping in mind single player mode. The bot for the single player mode plays randomly by selecting any empty cell
5. The two player mode was implemented
6. The dashboard was updated to show the list of open games and the stats of the player
7. Fixes were made along with ad-hoc testing of the application

## Accessibility
The application was built while keeping accessibility in mind. It was ensured that relevant items on
screen were marked as important for accessibility. The app was tested using Talkback and Accessibility
Scanner. The app was found to be accessible.

### Accessibility Scanner

On an initial run of the accessibility scanner, the following issues were found:

1. Item Label - On the recycler view, i.e, the list of open games. This was fixed
2. Image Contrast - 
   1. The contrast ratio of the FAB was too low. This was fixed
   2. Another warning came on the overflow menu icon button, this was ignored.
3. Text Scaling -
   1. Toolbar - This was ignored as this was a warning showing up on the default action bar.
   2. Text on the log out button - This was ignored as the text is not being set manually, and is being handled by the system.
   3. All edit texts on login/register - This was fixed by using a combination of wrap_content and min_width/height
4. Text Contrast - On the app name in the toolbar. This was ignored as the text is not being set manually, and is being handled by the system.
5. Unexposed Text - On the X and O buttons in the game fragment. This was fixed by setting the content description of the buttons.

### Experience using Talkback
I found the app fairly easy to use with my eyes closed and Talkback turned on. The buttons and fields
were large enough such that they were easy to select while using Talkback.

## Testing Approach
Once the login and register functionality was built, unit testing and integration testing was done for the same.

## Time Taken
50 hours

## Pair Programming Used
4/5

## Assignment difficulty
9.5/10

## Attribution
- [Assignment Documentation]()  
- Class Slides & Lectures
- ChatGPT & Claude
- [SO - How to change background color of the snackbar?](https://stackoverflow.com/questions/34020891/how-to-change-background-color-of-the-snackbar)
- [Firebase - Documentation](https://firebase.google.com/docs)
- [Firebase - Get started testing for Android with Firebase Test Lab](https://firebase.google.com/docs/test-lab/android/get-started)
