Iterator it = new Iterator();
// it: State "HasNext"
if (it.hasNext()) {
  // it: State "Next"
  it.next();
} else {
  // it: Ended
}