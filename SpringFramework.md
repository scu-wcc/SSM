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
		
5.在XML配置文件中引入并使用properties等配置文件的属性(同名属性会覆盖)	
	
	1.在XML中开启context命名空间
	2.使用<context:property-placeholder location="classPath:文件名">, *.类型标识读取所有该类型的配置文件
	3.在需要填入的值中使用"${属性名}"，如value = "${mysql.user}"
		
6.BeanFactory：所有容器的顶层接口，用作容器时所有bean都是延迟加载，即要注入时才创建bean对象存入容器中。
  ApplicationContext作为容器是即时加载。
		
		
注意：bean通过类型注入时（比如getBean(Class<T>)）可以传递接口类型或者父类类型，spring会返回对应实现类/子类的实例对象。
	  但该接口或父类在容器中只能有一个实现类/子类，否则报错。
	  
-例如：当使用getBean(Class<T> requiredType)方法时，Spring会扫描所有已注册的bean，检查它们的实际类型是否与requiredType匹配。这里的“匹配”包括：

	直接匹配：Bean的类与requiredType相同。
	接口匹配：Bean的类实现了requiredType接口。
	父类匹配：Bean的类是requiredType的子类。
	因此，即使你注册的是ServiceImpl，Spring会记录它的类型信息（包括它实现的接口Service），
	使得通过接口类型Service.class也能找到该bean，并且返回ServiceImpl的实例。

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
	