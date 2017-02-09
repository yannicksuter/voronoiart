package ch.suterra.art.voronoi.ui;

import ch.suterra.art.voronoi.VoronoiApp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by yannick on 08.02.17.
 */
public class VoronoiConfig extends JPanel {
	private final JLabel m_particleCountLabel;

	public VoronoiConfig(final VoronoiApp app) {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0,10,0,0);  //top padding
		add(new JLabel("Particle Count:"), c);

		m_particleCountLabel = new JLabel("10");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 1;
		c.insets = new Insets(0,10,0,0);  //top padding
		add(m_particleCountLabel, c);

		JSlider particleCount = new JSlider(JSlider.HORIZONTAL, 1, 100, app.getParticleCount());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 2;
		c.insets = new Insets(0,0,0,0);
		add(particleCount, c);

		particleCount.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				int particleCount = (int) source.getValue();
				m_particleCountLabel.setText(String.valueOf(particleCount));
				if (!source.getValueIsAdjusting()) {
					app.setParticleCount(particleCount);
					app.generateGraph();
				}
			}
		});

		JButton generateButton = new JButton("Generate new structure");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 3;
		c.insets = new Insets(10,0,0,0);
		generateButton.setMnemonic(KeyEvent.VK_G);
		add(generateButton, c);

		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.generateSeed(null);
				app.generateGraph();
			}
		});

		JButton exportButton = new JButton("Export SCAD");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.insets = new Insets(0,0,0,0);
		exportButton.setMnemonic(KeyEvent.VK_E);
		add(exportButton, c);

		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.export();
			}
		});
	}
}
