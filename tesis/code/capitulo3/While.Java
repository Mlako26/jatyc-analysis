Iterator it = new Iterator();
// it: State "HasNext"
while (it.hasNext()) {
  // it: State "Next"
  it.next();
  // it: State "HasNext"
}
// it: Ended