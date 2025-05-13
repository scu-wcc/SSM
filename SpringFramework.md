#SpringFramewor学习

1.核心概念

为了实现模块之间的解耦，提出IoC和DI思想：

	Ioc:控制反转。将对象的创建权由程序转移到外部，由外部控制对象的创建。
	-Spring使用IoC容器管理对象的创建和初始化过程，并将在IoC容器中被创建/管理的对象称为Bean。

	DI:依赖注入。在容器中建立Bean与Bean之间依赖关系的过程。
	-非bean类实现注入使用容器对象.getBean(id),
	-bean类实现内部属性注入使用setter或者构造器注入

XML实现IoC：
	
	1.在pom中引入Spring坐标，在resource中生成spring的配置文件xml
	2.在XML配置文件中使用<bean id="Bean名" class="要注入的对象全类名，要实现类作为对象放入容器中"></bean>
	3.调用时使用ApplicationContext调用方法传递配置文件，获取IoC容器
	4.通过IoC容器.getBean(id)方法获取被注入的Bean对象。
	
XML实现嵌套Bean的DI：
	
	在XML实现IoC的基础上，添加配置文件信息：
	<bean id="bookService" class="com.itheima.service.impl.BookServiceImpl">
    <!--7.配置server与dao的关系-->
    <!--property标签表示配置当前bean的属性
     name属性表示配置哪一个具体的属性
     ref属性表示参照哪一个bean-->
    <property name="bookDao" ref="bookDao"/>
    </bean>
	Spring自动调用bookService中的setBookDao方法注入。

2.Bean(XML版本)

Bean的配置：

	别名配置:Bean的别名效果和原名相同。
	-在<bean id=""  name="xxx,xxx" class...>中name字段规定别名，可以取多个，使用" "、","、";"分割都可。
		
	作用范围scope:用于控制注入的Bean对象是同一个还是每次都创建新对象注入。默认使用同一个singleton。
	-<bean id="" name="" class="" scope=""> 
	-不适合交给IoC管理的Bean：有状态的对象，比如封装实体的域对象，每个实体属性不同，导致需要使用的对象不同。
		
Bean的实例化：
	
	方法一(常用的):使用无参构造
	-spring通过反射获取该类的无参构造方法(private和public都可以)创建对象。如果不存在无参构造方法就会报错。
	
	方法二(了解)：静态工厂。使用工厂类的静态方法创建。可以在创建对象前进行其他配置。
	-配置一个工厂类，编写一个返回所需对象的静态方法。
	-配置<bean>标签:<bean id="xxx" class="工厂类的全限定类名" factory-method="指定创建对象的静态方法"/>
		
	方法三(了解):实例工厂。使用工厂类的非静态方法创建。需要先实例化工厂，通过工厂对象调用方法创建所需类对象。
	-配置一个工厂类，编写一个返回所需对象的非静态方法。
	-需要两个<bean>标签
		1.<bean id="" class="工厂全限定类名">用于创建工厂Bean
		2.<bean id="" factory-bean="工厂Bean的id" factory-method="调用的方法名"/ > 用于创建所需的bean对象。
		
	方法四(实用):spring提供了FactoryBean接口。创建的工厂类需要实现FactoryBean<T>接口，指定要生成的Bean的类型
	-实现类至少需要重写两个方法:
		1.getObject()：返回指定的Bean对象：return new xxxxx();
		2.getObjectType:返回指定Bean对象的类型: return xxxx.class;
	-XML中只需要<bean id="工厂名" class="工厂全限定类名">即可。	
		
Bean的生命周期控制:bean初始化后和销毁前的一些操作
	
	方法一：使用标签属性: init-method="方法名" destroy-method="方法名" 指定对应生命周期要执行的操作
	方法二：实现接口: InitializingBean,DisposableBean，重写初始化和销毁方法，spring自动执行。
	
	销毁方法需要在容器被关闭时才执行:
	-关闭容器的方法:
		-暴力关闭：容器对象.close()，直接关闭容器。
		-钩子方法: 容器对象.registerShutdownHook(),约定在虚拟机退出前关闭。	
	
bean生命周期：
			
	初始化容器：
		1.创建对象，分配内存。
		2.执行构造方法，初始化属性。
		3.执行属性注入(set操作，注入其他的bean等)
		4.执行bean的初始化方法
	使用bean：
		1.执行业务逻辑
	关闭/销毁容器：
		1.执行bean销毁方法
		
3.bean中嵌套bean依赖注入方式
			
setter注入：setter注入格式比较固定。

	对于引入数据类型和基本数据类型都相同
	-引入数据类型: <property name = "变量名" ref="对应类型的bean的id值">
	-基本数据类型(包括String)：<property name="变量名" value="value">，值写在""中，spring自动进行类型转换。
	-在类中提供对应的set方法，spring自动执行。
	
构造器注入：与setter注入类似，使用constructor-arg标签
	
	-类中不提供属性对应的set方法，或者要强制注入，避免属性为null的情况。
	-创建带参的构造方法，设置this.xxx=xxx;
	-在XML中对应的bean中配置
		引入数据类型：<constructor-arg name="构造方法中参数的名字" ref="指定注入bean的id值">
		基本数据类型：<constructor-arg name="构造方法中参数的名字" value="xxx">
	-了解：两种类型都可以不指定name，而是指定type:要注入的参数类型全限定类名，或者指定index="0,1,2,3...":要注入的参数的位置。

依赖自动装配：bean属性:autowire="byName/byType"
	
	-自动根据IoC容器中的Bean为变量注入，但只能注入引用数据类型。
	-使用byName需要变量名(set方法的setxxx中xxx)与容器中bean的id值相同。
	-使用byType需要保证容器中同类型的bean唯一。
		
集合注入：往集合中注入一些初始化值

数组、list、set、map、properties等格式类似
	<bean>
	<property name="bean中对应的集合变量名">
	<array> //集合类型
		<value>...</value>
		<value>...</value>
		......
		//map使用：<entry key="" value=""/>;
		//properties使用: <prop key ="">value</prop>
		//引入数据类型<ref bean="id">
	</array>
	<property>
	</bean>
		
4.管理第三方bean:需要自己去研究使用哪一种注入(setter or 构造器)
	

<span id="jump1">5.在XML配置文件中引入并使用properties等配置文件的属性(同名属性会覆盖)</span>
	
	1.在XML中开启context命名空间
	2.使用<context:property-placeholder location="classPath:文件名">, *.类型标识读取所有该类型的配置文件
	3.在需要填入的值中使用"${属性名}"，如value = "${mysql.user}"
		
6.BeanFactory：IoC容器的顶层接口，用作容器时所有bean都是延迟加载，即要注入时才创建bean对象存入容器中。
  ApplicationContext是spring的核心容器，即时加载Bean。
		
		
注意：bean通过类型注入时（比如getBean(Class<T>)）可以传递接口类型或者父类类型，spring会返回对应实现类/子类的实例对象。
	  但该接口或父类在容器中只能有一个实现类/子类，否则报错。
	  
-例如：当使用getBean(Class<T> requiredType)方法时，Spring会扫描所有已注册的bean，检查它们的实际类型是否与requiredType匹配。这里的“匹配”包括：

	直接匹配：Bean的类与requiredType相同。
	接口匹配：Bean的类实现了requiredType接口。
	父类匹配：Bean的类是requiredType的子类。
	因此，即使你注册的是ServiceImpl，Spring会记录它的类型信息（包括它实现的接口Service），
	使得通过接口类型Service.class也能找到该bean，并且返回ServiceImpl的实例。

7.注解开发

早期spring使用注解开发需要在配置文件中开辟命名空间，指定扫描的bean包路径，并且要指定Bean名，否则要用类别获取bean。
		
纯注解开发模式：将XML配置文件转换成Java配置类
		
	@Configuration:代表了XML配置文件的整体结构
	@ComponentScan(.....)==原配置文件中指定扫描bean的包路径的标签<context:component-scan .......>
	@PropertiySource({"配置文件类路径1","....."})，相当于第五点，之后bean中就可以通过"${}"来使用配置文件中的属性了(#id=jump1)
	@Import(其他类)：可以导入其他类的bean

8.bean管理		

	@Bean：将方法返回值注册为bean对象。
	@Component:将该类注册为bean	
	@Scope:作用域，单列 or 非单例
	@PostConstruct:注解方法，指定该方法为init初始化方法
	@PreDestroy:注解方法，指定该方法为销毁方法。

9.自动装配
	
	1.@Autowired实现自动注入，默认使用类型注入，存在同类型报错。
	-搭配@Qualifier(bean名字)可以指定要注入的bean。
	-底层基于反射创建对象，并暴力反射为对应的私有属性初始化数据，不需要setter方法。
	-推荐使用无参构造
	-@Value("${xxx}")与配置类中对应的配置文件注解使用，配置自动注入时该基本类型的值。
		
10.第三方Bean管理	
		
	一般使用一个单独的类来控制，然后在配置类中@Import({控制类.class,......})
	控制类中写什么？总的说，就是@Bean
	
	1.@Bean注释方法，表示该方法的返回值作为bean存入容器中。
	2.方法中想要自动注入参数？分基本数据类型和引用数据类型
		-基本数据类型:控制类中声明变量+@Value
		-引用数据类型：声明在该方法的形参中，spring自动注入。!!!!!!!!!!

11.spring整合MyBatis
	
	需要导入JDBC和Mybatis与spring整合的依赖
			
	XML配置整合Mybatis、注解整合Mybatis
	两种方式类似，最主要是配置SqlSessionFactory，注册Bean到容器中。

	主要的两个bean：SqlSessionFactoryBean和MappperScannerConfigurer
		
12.spring整合JUnit:在src下创建test文件，一般被测类层级与在main包中层级一致。
	
	@Runwith(SpringJUnit4ClassRunner.class):指定类运行器
	@ContextConfiguration(classes=配置类.class)：指定spring的配置类，以使用bean
			
13.	AOP: Aspect Oriented Programming面向切面编程。
无侵入式编程：不惊动原设计的基础上为其进行功能增强。简化共性代码的开发
	
核心概念：
	-代理Proxy:SpringAOP的核心本质是采用代理模式实现。

	-连接点JoinPoint：广义:程序执行的任意位置，粒度为：执行方法、抛出异常等
					  SpringAOP:各类方法
			 
	-切入点Pointcut：SpringAOP中切入点负责匹配要追加功能的方法(连接点)
	
	-通知Advice：要在切入点执行的操作，也就是增添功能的方法
	
	-通知类@Aspect：包含通知方法的类
	
	-切面Aspect：描述通知与切入点的对应关系。切入点->(切面)<-通知
		
基本实现：
	
	1.准备好基本的连接点，和通知方法
	2.配置切入点@Pointcut("excution(要连接的连接点)")，将切入点放在一个无返回值，无参数，无方法体的方法上。
	3.配置切面@Before/@Aroud/.....("切入点的方法名")
	4.将该切面类注册成组件@Component，并告知spring@Aspect该组件执行AOP操作。个人认为是为了生成动态代理对象Bean
	5.在配置类中@EnableAspectJAutoPoxy，告知spring存在使用注解开发的AOP
		
AOP工作流程：核心机制是使用代理对象

	1.Spring容器启动
	2.读取所有切面配置中的切入点
	3.初始化bean，判断bean对应类中的方法是否能匹配任意被读取的切入点。
		-无法匹配：创建原对象
		-成功匹配：创建原对象的代理对象作为bean
	4.获取bean执行方法
		-根据当前是原类对象还是代理对象调用方法。代理对象可以调用增强后的方法。
	
切入点表达式:excution(x xx..x())指定要切入的方法
	-excution(修饰符(可省) 返回值类型 包名..类名(参数) 异常类型(可省))
	-*:通配符，通配包名时表示任意但必须有一个。
	-..:通配符，可以表示多级多个。
	-'+':匹配子类/实现类
	
	!切入点表达式匹配接口方法和实现类方法是相同效果。!


通知类型：方法前/方法后/返回结果后/抛出异常后/环绕通知
	
	@Aroud：环绕通知，需要添加一个ProceedingJointPoin参数代表原方法。pjp.proceed():调用原方法。
	环绕通知需要接收返回值，并且返回返回值：生成的是代理对象，代理对象执行增强方法时不能改变原方法的结构。
	当然，如果不需要也可以不获取不返回。
	@Aroud("pt()")
	public Object handleMethod(ProceedingJointPoint pjp) throw Throwable {
		......
		Object result = pjp.proceed();
		......
		return result;	
	}
	-在环绕方法中通过pjp.getXXX可以获取被代理的该方法的一些信息。

通知获取数据：数据包括：参数，返回值，异常。环绕方法都可以获取，其他方法有的能获取，有的无法获取。
	环绕方法使用：ProceedJointPoint
	其他方法使用：JointPoint，并且必须设置为方法的第一个形参
		
14.事务：@Transactional注解表示spring中的事务。
	-需要到配置类中声明@EnableTransactionManagement，表示开启事务。
	-到管理类注册Spring事务管理器bean。
	-@Transactional一般使用在接口类上而非实现类，解耦合，子类继承的方法会自动开启事务。
	-spring的事务是可以到业务层的，对同一个业务下的多个数据操作统一管理

实现：事务管理员和事务协调员
	事务管理员(一般是业务层方法)发起事务T，将业务内部其他的事务(事务协调员)加入到T中形成同一个事务，就可以实现该业务的整体回滚和提交了。
		
相关属性配置：比较重要的如下两个属性
	-rollbackFor：需要回滚的异常。默认情况下spring只回滚error和RuntimeException，其他异常不回滚
	-propagation：事务的传播行为。事务协调员对于事务管理员新开事务的回应：加入统一管理的事务、新建事务独立运行、无反应、报错等
			
		
		
		
心得：	

	1本章节中将spring开发从XML开发转换到注解开发，本质上是将XML配置文件转换成配置类。
	那么对于需要注解才能生效的一些外部配置，例如@Aspect、@Transactional等，相当于XML中的一个独立大标签，
	都需要去配置类中使用对应的注解声明，相当于加了最外层的标签，才能生效。
	
	2.spring中大量地体现了多态与继承的关系：
	-对实现类/子类的定义(例如bean的注册)，可以通过接口/父类来调用获取；
	-对父类/接口的控制(例如AOP)，会扩展到子类；
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
	