using UnityEngine;
using UnityEditor;
using UnityEditor.SceneManagement;
using System.IO;
using System.Text;

/// <summary>
/// 导出navmesh寻路层
/// </summary>
public class PolygonNavMeshWindow : EditorWindow
{
    private int mapID;  //地图id
    private GameObject pathMesh;    //寻路网格对象

    //地图坐标范围
    public int startX=0;
    public int startZ=0;
    public int endX=300;
    public int endZ=300;

    [MenuItem("地图/多边形地图")]
    static void ShowToolWindow()
    {
        Rect rect = new Rect(0, 0, 500, 400);
        PolygonNavMeshWindow window = (PolygonNavMeshWindow)EditorWindow.GetWindowWithRect(typeof(PolygonNavMeshWindow), rect, true, "多边形寻路数据生成");
        window.Show();
    }

    
    public void Show()
    {
        base.Show();
        Init();
    }

    /// <summary>
    /// 初始化
    /// </summary>
    public void Init()
    {
        try
        {
            ClearTemp();
            string secenName=EditorSceneManager.GetActiveScene().name.Trim();
            char[] keys = { '_' };
            mapID = int.Parse(secenName.Split('_')[0]);

            //寻路层先根据标记查找，如果失败根据名称
            pathMesh= GameObject.FindGameObjectWithTag("terrain");
            if (pathMesh == null)
            {
                pathMesh = GameObject.Find("model_xzm_" + mapID);
            }
            if (pathMesh == null)
            {
                Debug.LogError("寻路层找不到，请确定tag设为terrain或名称设为model_xzm_地图id");
                Close();
                return;
            }
        }
        catch(System.Exception)
        {
            Close();
        }
    }

    private void OnDestroy()
    {
        mapID = 0;
        pathMesh = null;
    }

    /// <summary>
    /// 绘制工具面板
    /// </summary>
    private void OnGUI()
    {
        EditorGUILayout.LabelField("1.寻路层设置标记为terrain或名称为model_xzm_地图id");
        EditorGUILayout.LabelField("2.导出数据请刚好圈住寻路层");
        EditorGUILayout.Separator();
        EditorGUILayout.LabelField("地图ID：" + mapID);
        startX = EditorGUILayout.IntField("起始X",startX);
        startZ = EditorGUILayout.IntField("起始Z", startZ);
        endX = EditorGUILayout.IntField("结束X", endX);
        endZ = EditorGUILayout.IntField("结束Z", endZ);
        EditorGUILayout.LabelField("宽度：" + (endX - startX));
        EditorGUILayout.LabelField("高度：" + (endZ - startZ));       
        if (GUILayout.Button("测试大小"))
        {
            CreateMapTestMesh();
        }

        EditorGUILayout.Separator();
        if (GUILayout.Button("生成服务器数据"))
        {
            if (EditorSceneManager.GetActiveScene().name.Contains(mapID.ToString()))
            {
                CreatePolyNavMesh();
            }
        }

        EditorGUILayout.Separator();

        if (GUILayout.Button("重载配置"))
        {
            Init();
        }
    }

    /// <summary>
    /// 创建网格数据
    /// </summary>
    private void CreatePolyNavMesh()
    {
        UnityEngine.AI.NavMeshTriangulation triangulatedWalkNavMesh = Path();
        string path = System.Environment.CurrentDirectory.Replace("\\", "/") + "/../Config/Nav_build/";
        if (!Directory.Exists(path))
        {
            Directory.CreateDirectory(path);
        }
        StringBuilder sb = new StringBuilder("{");
        sb.Append("\"mapID\":").Append(mapID);
        sb.Append(",\"startX\":").Append(startX).Append(",\"startZ\":").Append(startZ);
        sb.Append(",\"endX\":").Append(endX).Append(",\"endZ\":").Append(endZ);
        string filename = path + mapID + ".navmesh";

        string data = "";
        data = PathMeshToString(triangulatedWalkNavMesh);
        sb.Append(",").Append(data);
        sb.Append("}");
        MeshToFile(filename, sb.ToString());
    }

    /// <summary>
    /// 清除临时对象
    /// </summary>
    public void ClearTemp()
    {
        GameObject mapTest=GameObject.Find("MapTest");
        if (mapTest != null)
        {
            Object.DestroyImmediate(mapTest);
        }
    }

    /// <summary>
    /// 创建测试网格
    /// </summary>
    private void CreateMapTestMesh()
    {
        GameObject ob= GameObject.Find("MapTest");
        if (ob == null)
        {
            ob= new GameObject("MapTest");
            ob.AddComponent<MeshFilter>();//网格
            ob.AddComponent<MeshRenderer>();//网格渲染器  
            
        }
        Mesh mesh = new Mesh();
        mesh.vertices = new Vector3[]
        {
            new Vector3(startX, 0, startZ),
            new Vector3(startX, 0, endZ+startZ),
            new Vector3(endX+startX, 0, endZ+startZ),
            new Vector3(endX+startX, 0, startZ)
        };
        mesh.triangles = new int[] { 0, 1, 2, 0, 2, 3 };
        ob.GetComponent<MeshFilter>().sharedMesh = mesh;
    }

    /// <summary>
    /// 计算行走层三角网格
    /// </summary>
    /// <returns></returns>
    private UnityEngine.AI.NavMeshTriangulation Path()
    {
        ClearTemp();
        pathMesh.GetComponent<Renderer>().enabled = true;
        UnityEditor.AI.NavMeshBuilder.ClearAllNavMeshes();
        UnityEditor.AI.NavMeshBuilder.BuildNavMesh();
        UnityEngine.AI.NavMeshTriangulation triangulatedNavMesh = UnityEngine.AI.NavMesh.CalculateTriangulation();


        return triangulatedNavMesh;
    }

    /// <summary>
    /// 寻路数据转换为字符串
    /// </summary>
    /// <param name="mesh"></param>
    /// <returns></returns>
    private string PathMeshToString(UnityEngine.AI.NavMeshTriangulation mesh)
    {
        if (mesh.indices.Length < 1)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.Append( "\"pathTriangles\":[");
        for (int i = 0; i < mesh.indices.Length; i++)
        {
            sb.Append(mesh.indices[i]).Append(",");
        }
        sb.Length--;
        sb.Append("],");

        sb.Append("\"pathVertices\":[");
        for (int i = 0; i < mesh.vertices.Length; i++)
        {
            Vector3 v = mesh.vertices[i];
      
            sb.Append("{\"x\":").Append(v.x).Append(",\"y\":").Append(v.y).Append(",\"z\":").Append(v.z).Append("},");
        }
        sb.Length--;
        sb.Append("]");
        return sb.ToString();
    }

    static void MeshToFile(string filename, string meshData)
    {
        using (StreamWriter sw = new StreamWriter(filename))
        {
            sw.Write(meshData);
        }
    }
}