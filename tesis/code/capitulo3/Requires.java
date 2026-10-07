
public void moveRight(@Requires("TopLeft") Robot robot) {
  robot.moveRight();
}

private void moveLeft(@Requires({"TopRight", "BotRight"}) Robot robot) {
  robot.moveLeft();
}


