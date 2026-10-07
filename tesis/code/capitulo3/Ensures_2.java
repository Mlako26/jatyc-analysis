public static void pushToStack(@Requires("Init") @Ensures("Init") final Stack stack, int e) {
  if (!stack.isFull()) {
    stack.push(e);
  }
}
