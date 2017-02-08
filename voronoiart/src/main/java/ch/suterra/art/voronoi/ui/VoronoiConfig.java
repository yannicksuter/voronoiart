package ch.suterra.art.voronoi.ui;

import ch.suterra.art.voronoi.VoronoiApp;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yannick on 08.02.17.
 */
public class VoronoiConfig extends JPanel {
	public static final int COLUMNS = 1;
	public static final int ROWS = 3;
	private static final int TF_COLS = 10;
	private static int inset = 5;
	private static final Insets INSETS = new Insets(inset, inset, inset, inset);
	private static final Insets EXTRA_INSETS = new Insets(inset, inset, inset, 8 * inset);
	private static final int EB_GAP = 10;

	public VoronoiConfig(VoronoiApp app) {
//		setLayout(new GridLayout(0, 2));
//		add(new JLabel("Particle Count:"));
//		JSlider particleCountSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
//		add(particleCountSlider);

		super(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(EB_GAP, EB_GAP, EB_GAP, EB_GAP));
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				addComponent(r, c);
			}
		}
	}

	private void addComponent(int r, int c) {
		int count = 1 + r * COLUMNS + c;
		JLabel label = new JLabel("label " + count);
		JTextField textField = new JTextField("value " + count, TF_COLS);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2 * c;
		gbc.gridy = r;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets = INSETS;
		gbc.weightx = 0.1;
		gbc.weighty = 1.0;
		add(label, gbc);

		gbc.gridx++;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		if (c != COLUMNS - 1) {
			gbc.insets = EXTRA_INSETS;
		}
		add(textField, gbc);
	}
}
