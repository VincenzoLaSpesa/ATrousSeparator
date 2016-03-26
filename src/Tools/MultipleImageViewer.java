/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Catalano.Imaging.FastBitmap;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Darshan
 */
public abstract class MultipleImageViewer {

    public static void show(List<FastBitmap> imgs, List<String> captions) {
        show(imgs, captions, 0);
    }

    public static void show(List<FastBitmap> imgs, List<String> captions, int tabNumber) {
        if (imgs == null) {
            imgs = new ArrayList<>();
        }
        if (captions == null) {
            captions = new ArrayList<>();
        }
        int N = Math.max(imgs.size(), captions.size());

        JPanel myPanel = new JPanel();

        if (tabNumber > 0) {
            GridLayout mgr=new GridLayout(0,tabNumber);
            mgr.setHgap(15);
            myPanel.setLayout(mgr);
        }

        for (int n = 0; n < N; n++) {
            JLabel l = new JLabel();
            if (imgs.size() > n) {
                l.setIcon(imgs.get(n).toIcon());                
            }
            l.setVerticalTextPosition(SwingConstants.BOTTOM);
            l.setHorizontalTextPosition(SwingConstants.CENTER);
            if (captions.size() > n) {
                l.setText(captions.get(n));
            } else {
                l.setText("_");
            }
            myPanel.add(l);
        }
        JOptionPane.showMessageDialog(null, myPanel);
    }
}
