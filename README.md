# sm-team-finder

Splinterlands bot sources 

## Note from author

1) This was 'hobby-coded' when the original author was first learning the game and kept adding in additions since, not exactly written as if it were a professional product or to be used by others.
2) There ARE some bugs in it.  Not every game will validate properly from past games.  There is a BattleTests class to run tests to hunt down faulty match simulations to then inspect and trace the game to assist in hunting down bugs.
3) The newest skill and Summoner will need to be added into the code.
4) Prior to handing off the project, a quick sweep of the code on a computer w/o Java was done, so there might be possible compile issues.  Probably just renaming or adding some small variable back in or the like.
5) The DynamoDB query portion is not valid.  It was what I was last working on and had issues.  An SQL Database would be suggested that models the domain object in the project.
6) The TeamFinderFlow is the main flow of obtaining suggested teams.  Some steps can be removed, or newly created ones added in here.
7) In hindsight, many things could have been done better :wink:


