package com.example

object BilingualData {

    data class AcademicContent(
        val title: String,
        val authors: String,
        val section1Title: String,
        val p1_1: String,
        val p1_2: String,
        val section2Title: String,
        val formula: String,
        val p2_1: String,
        val p2_2: String,
        val section3Title: String,
        val p3_1: String,
        val p3_2: String,
        val refsTitle: String,
        val referenceList: List<String>
    )

    val paperHciZh = AcademicContent(
        title = "信息熵与极简人机交互界面研究",
        authors = "Dr. Julian Vance & Prof. Clara Thorne",
        section1Title = "1. 摘要与引言",
        p1_1 = "学术界长期面临工具噪音泛滥的问题。传统数字终端堆砌了过度的检索控件与导航层级，增加了阅读过程中的认知负荷。本文探讨了学术阅读界面中“信息熵”（Information Entropy）的动态控制原理。我们认为，冗余的非文本控件是学者精神疲劳的主要诱因。",
        p1_2 = "为了提供更具聚焦度的环境，我们设计了一种无感化的物理空间切换机制。在该空间中，用户通过大阻尼的单指滑动来调整视觉维度，使阅读重新回归于本质。本文将对这一交互原型的心理物理学效应进行量化评估。",
        section2Title = "2. 研究方法与数学计算模型",
        p2_1 = "为了定量评估界面认知熵的值，我们引入了香农提出的信息不确定度公式作为算法基础。界面的视觉元素数量及其分布概率可以通过香农不确定度进行测量：",
        formula = "H(X) = - ∑ P(x_i) log_2 P(x_i)",
        p2_2 = "在评估受试者对界面跳转的适应情况时，我们通过施加不同的物理阻尼函数来测试肌肉记忆的撤销决策时间。实验组使用基于幂次衰减曲线的非线性阻力模型。数据表明，弹性阻力模型能够给大脑提供持续的边界触觉，从而将误操作率降低至原先的四分之一。",
        section3Title = "3. 实证分析与引用文献",
        p3_1 = "我们在20名学术工作者中开展了对比对照试验。相较于传统的控制栏页面导航，空间手势画布将学术文献检索的聚焦时间均值提升了34.6%，视觉搜索的耗时显著缩短。",
        p3_2 = "这一实验支持了去中心化导航的设计取向，也为下一代高信息密度的学术阅读终端提供了触觉映射的物理设计佐证。后续的研究工作将主要集中在三维知识图谱拓扑结构的无级手势捏合上。",
        refsTitle = "引用文献",
        referenceList = listOf(
            "[1] Shannon, C. E. (1948). A Mathematical Theory of Communication. Bell System Technical Journal.",
            "[2] Vance, J. (2022). Minimalism and Cognitive Friction in High-Density Reading Interfaces. HCI Review."
        )
    )

    val paperHciEn = AcademicContent(
        title = "Information Entropy and Minimalist User Interfaces",
        authors = "Dr. Julian Vance & Prof. Clara Thorne",
        section1Title = "1. Abstract & Introduction",
        p1_1 = "In modern academia, scholarly workflows are heavily hindered by interface friction. Digital reading portals are populated by bloated filters and redundant controls, which inevitably leads to acute cognitive burden. This paper evaluates the physical feedback mechanics and the systematic reduction of information entropy in digital document systems. We assert that non-essential GUI controls are the primary triggers for fatigue.",
        p1_2 = "To support persistent focus, we present a spatial-gesture framework that maps functional divisions to discrete physics-based canvases. The continuous layout supports heavy tactile boundaries. This model facilitates deep attention by allowing natural, low-noise muscle gestures. In this text, we evaluate this framework's performance via formal psychological tests.",
        section2Title = "2. Methodology & Mathematical Models",
        p2_1 = "To accurately analyze and quantify interface noise, we construct an evaluation metric utilizing Shannon's entropy formula. The density and distribution probability of arbitrary visual components can be mapped directly as:",
        formula = "H(X) = - ∑ P(x_i) log_2 P(x_i)",
        p2_2 = "While examining the user's cancelation decisions, we tested spatial recovery curves with diverse mathematical multipliers. Comparative studies show that power-law decay functions offer an intuitive 'spring' feeling that gives immediate cognitive affordance, which successfully reduces visual errors by 75%.",
        section3Title = "3. Empirical Analysis & References",
        p3_1 = "We executed an empirical assessment on 20 active researchers. Contrast trials showed that gestural navigation scales user focus span by an average of 34.6% against conventional scroll-based designs, resulting in a distinct decrease in search fatigue.",
        p3_2 = "This evidence validates decentralized and low-entropy interface philosophies, framing structural gestural physics as an appropriate medium for subsequent research. Future work will focus on three-dimensional document topology scaling and elastic pinch-to-zoom nodes.",
        refsTitle = "References",
        referenceList = listOf(
            "[1] Shannon, C. E. (1948). A Mathematical Theory of Communication. Bell System Technical Journal.",
            "[2] Vance, J. (2022). Minimalism and Cognitive Friction in High-Density Reading Interfaces. HCI Review."
        )
    )

    val paperCogZh = AcademicContent(
        title = "高维检索中的概念空间构建与认知映射",
        authors = "Dr. Julian Vance & Prof. Clara Thorne",
        section1Title = "1. 摘要与引言",
        p1_1 = "在大规模学术文献检索中，学者往往偏离自身的课题核心，迷失在高维学术信息流中。这是典型的“认知重组过载”。人脑构建学术知识关联本质上依赖于二维或三维物理空间的空间投影。现有的数字图书系统严重割裂了这种天然的物理直觉。",
        p1_2 = "为了解耦这层层级屏障，本研究着重探讨人脑建立跨学科关联时大脑神经元群的激活分布。通过利用高度集成的双轴阻力拓扑层，我们将学术文献及其引用路径投影在一个受物理拉力约束的网格空间中。本文详尽探讨了该物理映射机制的实证表现。",
        section2Title = "2. 方法论与高维拓扑投影",
        p2_1 = "我们将每一个文献条目特征表示为一个多维度向量 V，并利用降维映射算法将其坐标映射在 (X, Y) 的低维平面。学术条目之间的语义距离遵循高维特征向量的空间距离度量式：",
        formula = "D(U, V) = √ ( ∑ (u_i - v_i)² )",
        p2_2 = "为了提供边界感知，我们将手势物理学引入投影网络，在边界上引入了类似于高斯引力源的阻力衰减场。该阻力模型极大减少了操作的不确定度，并通过指尖的反馈暗示了信息网络中局部知识簇的紧密程度。",
        section3Title = "3. 実証分析与后续构想",
        p3_1 = "在人机工效测试中，实验组用户（在平滑阻阻尼物理投影下检索）相比于对照组（使用传统列表检索），对知识分类的结构性复述准确性从 48% 增加到 82%。",
        p3_2 = "这一结果强力支持了“体验空间化”观点。下一步，团队将探讨利用多点轻触捏合来实现多层文献网路的三维无级缩放与粒子流动态可视化。",
        refsTitle = "引用文献",
        referenceList = listOf(
            "[1] Gärdenfors, P. (2000). Conceptual Spaces: The Geometry of Thought. MIT Press.",
            "[2] Vance, J. (2023). Cognitive Spatial Maps in Knowledge Repositories. Journal of Cognitive Design."
        )
    )

    val paperCogEn = AcademicContent(
        title = "Constructing Conceptual Space in High-Dimensional Search",
        authors = "Dr. Julian Vance & Prof. Clara Thorne",
        section1Title = "1. Abstract & Introduction",
        p1_1 = "Scholars routinely face cognitive overload during high-dimensional academic searches, diverting from their primary research paths. Constructing structural associations relies heavily on human spatial cognitive systems. However, standard linear search grids decouple this deep physical intuition.",
        p1_2 = "To bridge this gap, this study investigates neuro-spatial mapping under high informational entropy. We present a dynamic multi-axial grid governed by physics-based tactile thresholds, mapping abstract academic papers to a continuous drag-based surface. This paper documents the psychophysical assessment of our gestural interaction prototype.",
        section2Title = "2. Methodology & Topology Projections",
        p2_1 = "We model arbitrary paper features as dense high-dimensional vectors V, projecting them to accessible coordinates (X,Y) via non-linear mapping. The relative conceptual overlap of research clusters is determined via standard distance formulas:",
        formula = "D(U, V) = √ ( ∑ (u_i - v_i)² )",
        p2_2 = "By wrapping the edge boundary in an elastic force-field based on Gaussian gravity wells, we construct physical 'walls' that imply the spatial limitations of the knowledge base. This feedback anchors the scholar's mental context, lowering memory overhead.",
        section3Title = "3. Empirical Evaluation & Outlook",
        p3_1 = "In interactive tests, researchers utilizing our physics-based mapping achieved an 82% conceptual retention accuracy, compared to a mere 48% with conventional tables, validating the tactile-spatial spatial reading schema.",
        p3_2 = "These insights demonstrate the value of tactile spatial mapping in knowledge exploration. Future lines of inquiries will explore pinch gestures for structural node clustering and continuous three-dimensional topology scaling.",
        refsTitle = "References",
        referenceList = listOf(
            "[1] Gärdenfors, P. (2000). Conceptual Spaces: The Geometry of Thought. MIT Press.",
            "[2] Vance, J. (2023). Cognitive Spatial Maps in Knowledge Repositories. Journal of Cognitive Design."
        )
    )

    fun getPaperContent(id: String, language: String): AcademicContent {
        return if (id == "hci") {
            if (language == "ZH") paperHciZh else paperHciEn
        } else {
            if (language == "ZH") paperCogZh else paperCogEn
        }
    }
}
