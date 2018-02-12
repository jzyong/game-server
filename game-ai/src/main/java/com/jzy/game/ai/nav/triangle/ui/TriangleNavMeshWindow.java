package com.jzy.game.ai.nav.triangle.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jzy.game.ai.nav.NavMeshData;
import com.jzy.game.ai.nav.triangle.TriangleNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;

/**
 * 地图显示窗口
 *
 * @author JiangZhiYong
 */
public class TriangleNavMeshWindow {

	private static final Logger LOGGER = LoggerFactory.getLogger(TriangleNavMeshWindow.class);

	static JPanel p = new JPanel();

	protected JFrame frame;
	protected TriangleViewPane triangleViewPane;
	protected JScrollPane jScrollPane;
	protected volatile boolean keepRunning = true;
	protected boolean pause = false;

	protected final Object mutex = new Object();
	protected ArrayList<AWTEvent> events = new ArrayList<>();
	protected ArrayList<AWTEvent> eventsCopy = new ArrayList<>();
	protected Vector3 curMouseMovePoint = new Vector3();

	protected Random rand = new Random(11);
	protected Vector3 curPoint;
	protected JTextField jtx;
	protected JFileChooser chooser;
	protected String choosertitle;
	protected TrianglePlayer player;
	protected TriangleNavStart main;
	/** 缓存上次加载文件 */
	public static String lastFilePath = "";

	public TriangleNavMeshWindow(TriangleNavStart main) {
		this.main = main;
		main.setMapWindow(this);
		MenuBar menuBar = new MenuBar();

		Menu menu = new Menu();
		MenuItem file = null;
		try {
			menu.setLabel(new String("菜单".getBytes(), "UTF-8"));
			file = new MenuItem();
			file.setLabel("选择NavMesh");
		} catch (Exception e) {
			e.printStackTrace();
		}

		menu.add(file);

		file.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result;
				chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle(choosertitle);
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				if (chooser.showOpenDialog(p) == JFileChooser.APPROVE_OPTION) {
					// log.error("getCurrentDirectory(): "
					// + chooser.getCurrentDirectory());
					// log.error("getSelectedFile() : "
					// + chooser.getSelectedFile());
					loadMap(chooser.getSelectedFile().getAbsolutePath(), 5);
				} else {
					LOGGER.error("No Selection ");
				}
			}
		});

		menuBar.add(menu);

		this.triangleViewPane = createViePane();

		frame = new JFrame("三角形navmesh寻路");
		jScrollPane = new JScrollPane(triangleViewPane);
		if (this.triangleViewPane.getPlayer().getMap() != null) {
			double width = Toolkit.getDefaultToolkit().getScreenSize().width; // 得到当前屏幕分辨率的高
			double height = Toolkit.getDefaultToolkit().getScreenSize().height;// 得到当前屏幕分辨率的宽
			triangleViewPane.setPreferredSize(new Dimension((int) width, (int) height));
			frame.setSize((int) width, (int) height);// 设置大小
		} else {
			frame.setSize(300, 300);// 设置大小
		}
		frame.setLocation(0, 0); // 设置窗体居中显示
		frame.setResizable(true);// 禁用最大化按钮

		frame.add(jScrollPane);

		frame.setMenuBar(menuBar);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				keepRunning = false;
				System.exit(0);
			}
		});
		frame.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}
		});
		frame.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void keyReleased(KeyEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void keyTyped(KeyEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}
		});
		triangleViewPane.addMouseListener(new MouseListener() {
			public void mousePressed(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void mouseClicked(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void mouseEntered(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void mouseExited(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}
		});
		triangleViewPane.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}

			public void mouseDragged(MouseEvent e) {
				synchronized (mutex) {
					events.add(e);
				}
			}
		});

		init();

		frame.setVisible(true);

		Thread gameLoopThread = new Thread("GameLoop") {
			public void run() {
				long lastUpdateNanos = System.nanoTime();
				while (keepRunning) {
					long currentNanos = System.nanoTime();
					float seconds = (currentNanos - lastUpdateNanos) / 1000000000f;
					processEvents();
					if (pause != true) {
						update(seconds);
					}
					try {
						Thread.sleep(1);
						triangleViewPane.render();
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
					lastUpdateNanos = currentNanos;
				}
			}
		};
		gameLoopThread.setDaemon(false);
		gameLoopThread.start();
	}

	/**
	 * 加载地图
	 * @param filePath
	 * @return
	 */
	public TriangleNavMesh loadMap(String filePath,int scale) {
		TriangleNavMesh map = null;
        lastFilePath=filePath;
		try {
			String navMeshStr = FileUtil.readTxtFile(filePath);
			map = new TriangleNavMesh(navMeshStr,scale);	
			if (map != null && player != null) {
				player.setMap(map);
				int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
				int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
				NavMeshData navMeshData= map.getGraph().getNavMeshData();
				triangleViewPane.setPreferredSize(new Dimension((int)(navMeshData.getWidth()*scale*(1+navMeshData.getHeight()/(navMeshData.getWidth()+navMeshData.getHeight()))), 
						(int)(navMeshData.getHeight()*scale*(1+navMeshData.getWidth()/(navMeshData.getWidth()+navMeshData.getHeight())))));
				frame.setSize(width, height);
//				frame.setPreferredSize(new Dimension(width, height));
				frame.setResizable(true);
				
			}
			return map;
		} catch (Exception e) {

			LOGGER.error("Map:" + e.toString());
			LOGGER.error("地图错误", e);
		}
		return null;
	}

	private TriangleViewPane createViePane() {
		TriangleNavMesh map = null;
		File file = new File(System.getProperty("user.dir"));

		String path = file.getPath();
		// path += File.separatorChar + "target" + File.separatorChar + "config" +
		// File.separatorChar + "navmesh" + File.separatorChar + "101.navmesh";
		path += File.separatorChar + "101.navmesh";
		map = loadMap(path, 5);

		player = new TrianglePlayer(map);

		return new TriangleViewPane(player);
	}

	public void init() {
		triangleViewPane.getPlayer().setPos(new Vector3(400, 280));
		triangleViewPane.getPlayer().setTarget(triangleViewPane.getPlayer().getPos().copy());
	}

	/**
	 * 随机移动
	 */
	public void randMove() {
		// TODO 暂时不支持
		// Vector3 t = view.getMap().getRandomPointInPaths(view.getPlayer().pos, 80, 2);
		// if (t != null) {
		// view.getPlayer().target.x = t.x;
		// view.getPlayer().target.z = t.z;
		// view.getPlayer().path();
		// }
	}

	public void processEvents() {
		synchronized (mutex) {
			if (events.size() > 0) {
				eventsCopy.addAll(events);
				events.clear();
			}
		}
		if (eventsCopy.size() > 0) {
			for (int i = 0; i < eventsCopy.size(); i++) {
				AWTEvent awtEvent = eventsCopy.get(i);
				if (awtEvent instanceof MouseEvent) {
					MouseEvent e = (MouseEvent) awtEvent;
					switch (e.getID()) {
					case MouseEvent.MOUSE_MOVED:
						break;
					case MouseEvent.MOUSE_PRESSED:
						curMouseMovePoint.x = e.getX();
						curMouseMovePoint.z = e.getY();
						break;
					case MouseEvent.MOUSE_DRAGGED:
						Vector3 p = triangleViewPane.getMap().getPointInPath(e.getX(), e.getY());
						if (p != null) {
							triangleViewPane.getPlayer().target.x = e.getX();
							triangleViewPane.getPlayer().target.z = e.getY();
							triangleViewPane.getPlayer().path();
						}
						break;
					case MouseEvent.MOUSE_CLICKED:
						/* 有缩放 */
						Vector3 positon = this.player.getMap().getPointInPath(e.getX(), e.getY());
						if (positon == null) {
							positon = new Vector3(e.getX(), e.getY());
						}
						main.setPosition(positon);
						triangleViewPane.getPlayer().pos.x = e.getX();
						triangleViewPane.getPlayer().pos.z = e.getY();
						break;
					default:
						break;
					}
				} else if (awtEvent instanceof java.awt.event.KeyEvent) {
					KeyEvent e = (KeyEvent) awtEvent;
					if (e.getID() == KeyEvent.KEY_PRESSED) {
						if (e.getKeyCode() == KeyEvent.VK_R) {
							this.init();
						}
						if (e.getKeyCode() == KeyEvent.VK_P) {
							if (pause == true) {
								pause = false;
							} else {
								pause = true;
							}
						}
					}
				} else if (awtEvent instanceof ComponentEvent) {
					ComponentEvent e = (ComponentEvent) awtEvent;
					if (e.getID() == ComponentEvent.COMPONENT_RESIZED) {
						this.init();
					}
				}
			}
			eventsCopy.clear();
		}
	}

	double totalSeconds = 0;

	public void update(float seconds) {
		triangleViewPane.getPlayer().update(seconds);
		totalSeconds += seconds;

	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
}
