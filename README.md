## ToME: Topology Measurement Environment

0. Download the jar and the example directory.
1. Launch the jar. Click Help and read through the vocabulary.
2. Click Load and select the "example.sif" to load the backing graph.
3. Open "sample.txt", copy and paste the list of genes into the Initial Filter textbox.
4. Choose Loose and click Filter.

You'll now see a list of genes with their clustering coefficient, average
path length, and degree. To make this more useful, we must trim the graph.

5. Click Trim, then check all of the trim options.

You should now have meaningful graph average properties, and sorting by clustering coefficient,
average path length or degree should bring up interesting sets of genes.

6. Sort by clustering coefficient, then Shift+Select those with coefficient > 0.
7. Hit view to see the topology of the selected nodes.
8. You can click Selected to see just the selected nodes, or Whole to see the entire graph.
9. Close the graph view and hit Ctrl to generate control sets for your subgraph.
10. Explore the control distributions using the drop-downs. The value of the metric for your selection is shown next to the drop-down.
11. Click "Save Control Stats" to get percentiles for each control statistic for subsequent significance analysis.


