package org.mtr.mod;

import com.jonafanho.apitools.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModUpload {

	private static final String[] MINECRAFT_VERSIONS = {"1.16.5", "1.17.1", "1.18.2", "1.19.2", "1.19.4", "1.20.1", "1.20.4"};

	public static void main(String[] args) throws IOException {
		if (args.length == 2) {
			for (final String minecraftVersion : MINECRAFT_VERSIONS) {
				for (final ModLoader modLoader : ModLoader.values()) {
					final String modVersion = String.format("%s-%s+%s", modLoader.name, args[0], minecraftVersion);
					final String modVersionUpperCase = String.format("%s-%s+%s", modLoader.name.toUpperCase(Locale.ENGLISH), args[0], minecraftVersion);
					final String fileName = String.format("Yunzhu-Transit-Extension-%s.jar", modVersion);
					final Path filePath = Paths.get("build/release").resolve(fileName);

					final Map<String, DependencyType> dependenciesModrinth = new HashMap<String, DependencyType>();
					if (modLoader == ModLoader.FABRIC) {
						dependenciesModrinth.put("P7dR8mSH", DependencyType.REQUIRED);
					}
					dependenciesModrinth.put("XKPAmI6u", DependencyType.REQUIRED);

					do {
					} while (!new ModId("nqMdKn6A", ModProvider.MODRINTH).uploadFile(
							modVersionUpperCase,
							modVersionUpperCase,
							"##安装本mod之前，请安装mtr4.0.0正式版！\n" +
									"\n" +
									"# ChangeLog 更新日志\n" +
									"\n" +
									"## 新增方块\n" +
									"\n" +
									"### Dewhurst 系列\n" +
									"- US91 呼梯面板（类型 1，含盲文）\n" +
									"\n" +
									"### 通力（KONE）系列\n" +
									"- KDS330 呼梯面板（外挂式，带/无屏幕）\n" +
									"- KDS330 到站灯（外挂式）\n" +
									"- KSS280 呼梯面板（外挂式，带/无屏幕）\n" +
									"- KSS280 候梯楼层显示器（占一格 / 两格）\n" +
									"\n" +
									"### 奥的斯（Otis）系列\n" +
									"- Series 3 呼梯面板（类型 1）\n" +
									"- Series 3 候梯楼层显示器（占一格 / 两格）\n" +
									"- Series 3 ELD 候梯楼层显示器（占一格 / 两格）\n" +
									"- Series 3 到站灯（有箭头，占一格 / 两格）\n" +
									"- SPEC60 呼梯面板（类型 1）\n" +
									"- SPEC90 呼梯面板（类型 1/2，黑色 / 白色）\n" +
									"- E411 US 电梯门（类型 1）\n" +
									"\n" +
									"### 三菱（Mitsubishi）系列\n" +
									"- GPS / GPM 呼梯面板（类型 1，带/无屏幕）\n" +
									"- 信兴广场呼梯面板\n" +
									"- 菱电升降机外显（占一格 / 两格）\n" +
									"- PIV1 系列呼梯面板（A710B/A720B, C710N/C720N, A1010B/A1020B）\n" +
									"- PIV3 系列呼梯面板（CS60N / CS70N）\n" +
									"- PIV 系列呼梯面板（C210/C220, C610/C620, C611/C621）\n" +
									"- HBV 系列呼梯面板（C210/C220）\n" +
									"- HLH-A16S 到站灯（占一格 / 两格）\n" +
									"- HLV-A16S 到站灯（占一格 / 两格）\n" +
									"- PIE-B47 候梯楼层显示器（占一格 / 两格）\n" +
									"- PIH 系列候梯楼层显示器（C116, D415, D417，占一格 / 两格）\n" +
									"- MP-VF 呼梯面板（类型 1）\n" +
									"- MP-VF 候梯楼层显示器（占一格 / 两格）\n" +
									"- 上海三菱 ZPIS 呼梯面板（C110 / C120）\n" +
									"\n" +
									"### 迅达（Schindler）系列\n" +
									"- M 系列候梯楼层显示器（类型 4，占一格 / 两格）\n" +
									"- Linea 系列呼梯面板（白色 / 黑色，带/无屏幕）\n" +
									"- Linea 系列候梯楼层显示器（白色 / 黑色，横向 / 纵向，占一格 / 两格）\n" +
									"\n" +
									"### 蒂森克虏伯（Thyssenkrupp）系列\n" +
									"- TE-GL1 呼梯面板（暂定）\n" +
									"\n" +
									"### 日立（Hitachi）系列\n" +
									"- VIB 系列呼梯面板（320/320W, 322/322W, 221/221W, 820/820W, 68/68W, 191/191W, 192/192W, 668/668W, 658/658W, 663/663W，含点阵 / LCD 分段）\n" +
									"- HB-820 呼梯面板\n" +
									"- HSB-820 / 820W 呼梯面板\n" +
									"- VIB-820pro / 820proW 呼梯面板\n" +
									"- HSB-820pro / 820proW 呼梯面板\n" +
									"- GHD-820pro 候梯楼层显示器（占一格 / 两格）\n" +
									"- GHI-675 候梯楼层显示器（占一格 / 两格）\n" +
									"- GHL-820 到站灯（占一格 / 两格）\n" +
									"- PAFC呼梯面版\n" +
									"\n" +
									"### 通力电子（Tonic）系列\n" +
									"- DS 候梯楼层显示器（占一格 / 两格）\n" +
									"- DM 候梯楼层显示器（红 / 绿 / 黄，占一格 / 两格）\n" +
									"\n" +
									"### PAT 铁路设施系列\n" +
									"- RS01 铁路导向标志（2/3/4/5/6/7，占一格 / 两格）\n" +
									"- RS01 铁路导向标志（中间 / 支柱）\n" +
									"\n" +
									"## 新增功能\n" +
									"\n" +
									"1. 支持使用 YTE 批量电梯连接器连接 MTR 电梯按钮  \n" +
									"2. 到站灯支持连接智能分配终端  \n" +
									"3. 支持使用 YTE 批量电梯连接器连接到站灯与电梯按钮（三点式操作）\n" +
									"\n" +
									"## 优化内容\n" +
									"\n" +
									"- 优化智能分配终端的派梯逻辑  \n" +
									"- 优化电梯按钮判定精准度  \n" +
									"- 优化电梯部件渲染性能  \n" +
									"\n" +
									"## 修复内容\n" +
									"\n" +
									"1. 修复部分版本无法启动的问题  \n" +
									"2. 修复 YTE 批量电梯连接器、批量楼层设置器计数提示错误的问题  \n" +
									"3. 修复按钮图层间距过大的问题  \n" +
									"\n" +
									"## 依赖更新\n" +
									"\n" +
									"- 将 MTR 版本依赖更新为 `4.0.0`  \n" +
									"\n" +
									"## Please install the official release of MTR version 4.0.0 before installing this mod!\n" +
									"\n" +
									"# ChangeLog\n" +
									"\n" +
									"## New Blocks\n" +
									"\n" +
									"### Dewhurst Series\n" +
									"- US91 hall call panel (Type 1, with Braille)\n" +
									"\n" +
									"### KONE Series\n" +
									"- KDS330 hall call panel (surface-mounted, with/without screen)\n" +
									"- KDS330 arrival indicator (surface-mounted)\n" +
									"- KSS280 hall call panel (surface-mounted, with/without screen)\n" +
									"- KSS280 lobby floor indicator (1-block / 2-block size)\n" +
									"\n" +
									"### Otis Series\n" +
									"- Series 3 hall call panel (Type 1)\n" +
									"- Series 3 lobby floor indicator (1-block / 2-block size)\n" +
									"- Series 3 ELD lobby floor indicator (1-block / 2-block size)\n" +
									"- Series 3 arrival indicator (with arrow, 1-block / 2-block size)\n" +
									"- SPEC60 hall call panel (Type 1)\n" +
									"- SPEC90 hall call panel (Type 1/2, black / white)\n" +
									"- E411 US elevator door (Type 1)\n" +
									"\n" +
									"### Mitsubishi Series\n" +
									"- GPS / GPM hall call panel (Type 1, with/without screen)\n" +
									"- Shun Hing Square hall call panel\n" +
									"- R&D Lift external indicator (1-block / 2-block size)\n" +
									"- PIV1 Series hall call panels (A710B/A720B, C710N/C720N, A1010B/A1020B)\n" +
									"- PIV3 Series hall call panels (CS60N / CS70N)\n" +
									"- PIV Series hall call panels (C210/C220, C610/C620, C611/C621)\n" +
									"- HBV Series hall call panels (C210/C220)\n" +
									"- HLH-A16S arrival indicator (1-block / 2-block size)\n" +
									"- HLV-A16S arrival indicator (1-block / 2-block size)\n" +
									"- PIE-B47 lobby floor indicator (1-block / 2-block size)\n" +
									"- PIH Series lobby floor indicators (C116, D415, D417, 1-block / 2-block size)\n" +
									"- MP-VF hall call panel (Type 1)\n" +
									"- MP-VF lobby floor indicator (1-block / 2-block size)\n" +
									"- Shanghai Mitsubishi ZPIS hall call panels (C110 / C120)\n" +
									"\n" +
									"### Schindler Series\n" +
									"- M Series lobby floor indicator (Type 4, 1-block / 2-block size)\n" +
									"- Linea Series hall call panels (white / black, with/without screen)\n" +
									"- Linea Series lobby floor indicators (white / black, horizontal / vertical, 1-block / 2-block size)\n" +
									"\n" +
									"### Thyssenkrupp Series\n" +
									"- TE-GL1 hall call panel (tentative)\n" +
									"\n" +
									"### Hitachi Series\n" +
									"- VIB Series hall call panels (320/320W, 322/322W, 221/221W, 820/820W, 68/68W, 191/191W, 192/192W, 668/668W, 658/658W, 663/663W, with dot-matrix / LCD segment displays)\n" +
									"- HB-820 hall call panel\n" +
									"- HSB-820 / 820W hall call panels\n" +
									"- VIB-820pro / 820proW hall call panels\n" +
									"- HSB-820pro / 820proW hall call panels\n" +
									"- GHD-820pro lobby floor indicator (1-block / 2-block size)\n" +
									"- GHI-675 lobby floor indicator (1-block / 2-block size)\n" +
									"- GHL-820 arrival indicator (1-block / 2-block size)\n" +
									"- PAFC hall call panel\n" +
									"\n" +
									"### Tonic Series\n" +
									"- DS lobby floor indicator (1-block / 2-block size)\n" +
									"- DM lobby floor indicator (red / green / yellow, 1-block / 2-block size)\n" +
									"\n" +
									"### PAT Railway Facility Series\n" +
									"- RS01 railway direction signs (Types 2/3/4/5/6/7, 1-block / 2-block size)\n" +
									"- RS01 railway direction signs (center / pillar versions)\n" +
									"\n" +
									"## New Features\n" +
									"\n" +
									"1. Support for using the YTE Group Elevator Connector to link MTR elevator buttons  \n" +
									"2. Arrival indicators can now connect to smart dispatch terminals  \n" +
									"3. Support for connecting arrival indicators and elevator buttons with the YTE Group Elevator Connector (three-point control)\n" +
									"\n" +
									"## Improvements\n" +
									"\n" +
									"- Improved elevator dispatch logic in the smart dispatch terminal  \n" +
									"- Enhanced detection accuracy of elevator buttons  \n" +
									"- Optimized rendering performance of elevator components\n" +
									"\n" +
									"## Bug Fixes\n" +
									"\n" +
									"1. Fixed startup issues in some versions  \n" +
									"2. Fixed incorrect count hints in YTE Group Elevator Connector and Floor Auto Setter  \n" +
									"3. Fixed excessive spacing between button layers\n" +
									"\n" +
									"## Dependency Update\n" +
									"\n" +
									"- Updated MTR dependency to `4.0.0`\n" +
									"\n",
							dependenciesModrinth,
							ReleaseStatus.BETA,
							Collections.singleton(minecraftVersion),
							Collections.singleton(modLoader),
							false,
							Files.newInputStream(filePath),
							fileName,
							args[1]
					));
				}
			}
		}
	}
}
