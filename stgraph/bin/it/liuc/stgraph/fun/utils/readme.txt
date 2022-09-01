These classes are taken from the Colt library and adapted to STGraph by:
-- renaming, if required, the class from * to *Distribution;
-- removing the subclassing
-- substituting the references to RandomEngine with Math.random()
-- maintaining only the pdf(), cdf(), and nextInt() / nextDouble() methods,
     adding the required parameters to them (check the order of parameters...),
     and making them static.
