package com.jzy.game.ai.nav.polygon.ui;

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
import com.jzy.game.ai.nav.polygon.Polygon;
import com.jzy.game.ai.nav.polygon.PolygonNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.FileUtil;

/**
 * 地图显示窗口
 *
 * @author JiangZhiYong
 */
public class PolygonMeshWindow {

	private static final Logger LOGGER = LoggerFactory.getLogger(PolygonMeshWindow.class);

	static JPanel p = new JPanel();

	protected JFrame frame;
	protected PolygonViewPane polygonViewPane;
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
	protected MovePlayer player;
	protected PolygonNavStart main;
	/** 缓存上次加载文件 */
	public static String lastFilePath = "";
        /**地图缩放倍数*/
        private int scale=1;

	public PolygonMeshWindow(PolygonNavStart main) {
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
					loadMap(chooser.getSelectedFile().getAbsolutePath(), 5);
				} else {
					LOGGER.error("No Selection ");
				}
			}
		});

		menuBar.add(menu);

		this.polygonViewPane = createViePane();

		frame = new JFrame("多边形navmesh寻路");
		jScrollPane = new JScrollPane(polygonViewPane);
		if (this.polygonViewPane.getPlayer().getMap() != null) {
			double width = Toolkit.getDefaultToolkit().getScreenSize().width; // 得到当前屏幕分辨率的高
			double height = Toolkit.getDefaultToolkit().getScreenSize().height;// 得到当前屏幕分辨率的宽
			polygonViewPane.setPreferredSize(new Dimension((int) width, (int) height));
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
		polygonViewPane.addMouseListener(new MouseListener() {
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
		polygonViewPane.addMouseMotionListener(new MouseMotionListener() {
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
						polygonViewPane.render();
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
	 * 
	 * @param filePath
	 * @return
	 */
	public PolygonNavMesh loadMap(String filePath, int scale) {
                this.scale=scale;
		PolygonNavMesh map = null;
		lastFilePath = filePath;
		try {
			String navMeshStr = FileUtil.readTxtFile(filePath);
			map = new PolygonNavMesh(navMeshStr, scale);
			if (map != null && player != null) {
				player.setMap(map);
				int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
				int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
				NavMeshData navMeshData = map.getGraph().getPolygonData();
				polygonViewPane.setPreferredSize(new Dimension(
						(int) (navMeshData.getWidth() * scale
								* (1 + navMeshData.getHeight() / (navMeshData.getWidth() + navMeshData.getHeight()))),
						(int) (navMeshData.getHeight() * scale
								* (1 + navMeshData.getWidth() / (navMeshData.getWidth() + navMeshData.getHeight())))));
				frame.setSize(width, height);
				// frame.setPreferredSize(new Dimension(width, height));
				frame.setResizable(true);

			}
			return map;
		} catch (Exception e) {

			LOGGER.error("Map:" + e.toString());
			LOGGER.error("地图错误", e);
		}
		return null;
	}

	private PolygonViewPane createViePane() {
		PolygonNavMesh map = null;
		File file = new File(System.getProperty("user.dir"));

		String path = file.getPath();
		// path += File.separatorChar + "target" + File.separatorChar + "config" +
		// File.separatorChar + "navmesh" + File.separatorChar + "101.navmesh";
		path += File.separatorChar + "101.navmesh";
		map = loadMap(path, 5);

		player = new MovePlayer(map);

		return new PolygonViewPane(player);
	}

	public void init() {
		polygonViewPane.getPlayer().setPos(new Vector3(400, 280));
		polygonViewPane.getPlayer().setTarget(polygonViewPane.getPlayer().getPos().copy());
	}

	/**
	 * 随机移动
	 */
	public void randMove() {
		Vector3 t = polygonViewPane.getMap().getRandomPointInPath(polygonViewPane.getPlayer().pos, 800, 2);
		if (t != null) {
			polygonViewPane.getPlayer().target.x = t.x;
			polygonViewPane.getPlayer().target.z = t.z;
			polygonViewPane.getPlayer().path();
		}
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
						Vector3 p = polygonViewPane.getMap().getPointInPath(e.getX(), e.getY());
						if (p != null) {
							polygonViewPane.getPlayer().target.x = e.getX();
							polygonViewPane.getPlayer().target.z = e.getY();
							polygonViewPane.getPlayer().path();
						}
						break;
					case MouseEvent.MOUSE_CLICKED:
						/* 有缩放 */
						Vector3 positon = this.player.getMap().getPointInPath(e.getX(), e.getY());
						if (positon == null) {
							positon = new Vector3(e.getX(), e.getY());
						}
						main.setPosition(positon);
						polygonViewPane.getPlayer().pos.x = e.getX();
						polygonViewPane.getPlayer().pos.z = e.getY();
						
						//双击渲染选中的多边形
						if(e.getClickCount()>1) {
							Polygon polygon = polygonViewPane.getPlayer().getMap().getPolygon(new Vector3(e.getX(), e.getY()));
							if(polygon!=null) {
								polygonViewPane.setRenderPolygon(polygon);
								
								main.showLog(polygon.print());
							}
						}
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
		polygonViewPane.getPlayer().update(seconds);
		totalSeconds += seconds;

	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}
        
        /**
         * 设置定位坐标
         * @param x
         * @param z 
         */
        public void setLocationVector(float x,float z){
            Vector3 pos= new Vector3(x*this.scale,0,z*this.scale);
            boolean pointInPath = polygonViewPane.getPlayer().getMap().isPointInPath(pos);
            this.main.showLog(String.format("坐标点%s 在行走层%s", pos.toString(),String.valueOf(pointInPath)));
            polygonViewPane.setLocationPosition(pos);
        }
}
