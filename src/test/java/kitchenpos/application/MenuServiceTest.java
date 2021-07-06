package kitchenpos.application;

import kitchenpos.dao.MenuDao;
import kitchenpos.dao.MenuGroupDao;
import kitchenpos.dao.MenuProductDao;
import kitchenpos.dao.ProductDao;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MenuServiceTest {
	@Autowired
	private MenuDao menuDao;

	@Autowired
	private MenuService menuService;

	@Autowired
	private MenuGroupDao menuGroupDao;

	@Autowired
	private MenuProductDao menuProductDao;

	@Autowired
	private ProductDao productDao;

	@Test
	public void 메뉴등록_성공() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		Menu savedMenu = menuService.create(menu);
		List<MenuProduct> menuProductList = savedMenu.getMenuProducts();

		assertThat(savedMenu.getMenuGroupId()).isEqualTo(menuGroup.getId());
		assertThat(menuProductList.size()).isEqualTo(1);
		assertThat(menuProductList).extracting("menuId")
				.containsOnly(savedMenu.getId());
		assertThat(savedMenu.getName()).isEqualTo("신상치킨x3");
		assertThat(savedMenu.getPrice()).isEqualByComparingTo(product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())));
	}

	@Test
	@DisplayName("메뉴의 목록을 조회할 수 있다.")
	public void 메뉴목록조회() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		menuService.create(menu);

		List<Menu> menus = menuService.list();
		assertThat(menus).isNotEmpty();
		assertThat(menus)
				.extracting("name")
				.contains("신상치킨x3");
	}

	@Test
	@DisplayName("메뉴등록 실패 가격은 Null 이되면 안됨")
	public void 메뉴등록_실패_가격은_Null이면안됨() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		assertThatThrownBy(() -> menuService.create(menu))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("메뉴등록 실패 가격은 0 이되면 안됨")
	public void 메뉴등록_실패_가격은_0이면안됨() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		assertThatThrownBy(() -> menuService.create(menu))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("메뉴등록 실패 메뉴그룹 세팅하지 않음")
	public void 메뉴등록_실패_없는메뉴그룹() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", product.getPrice().multiply(new BigDecimal(menuProduct.getQuantity())), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		assertThatThrownBy(() -> menuService.create(menu))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("메뉴등록 실패 메뉴의 가격은 메뉴에 등록된 상품리스트 가격의 총합과 같아야 한다.")
	public void 메뉴등록_실패_상품리스트_가격_총합은_메뉴가격과_같아야한다() {
		final List<MenuProduct> menuProducts = new ArrayList<>();

		MenuProduct menuProduct = new MenuProduct();
		Product product = 상품생성();

		menuProduct.setProductId(product.getId());
		menuProduct.setQuantity(3);

		menuProducts.add(menuProduct);

		MenuGroup menuGroup = 메뉴그룹생성();

		Menu menu = new Menu("신상치킨x3", new BigDecimal("9999999999"), menuGroup.getId());
		menu.setMenuProducts(menuProducts);

		assertThatThrownBy(() -> menuService.create(menu))
				.isInstanceOf(IllegalArgumentException.class);
	}

	private MenuGroup 메뉴그룹생성() {
		MenuGroup menuGroup = new MenuGroup();
		String menuGroupname = "추천메뉴";
		menuGroup.setName(menuGroupname);

		return menuGroupDao.save(menuGroup);
	}

	private Product 상품생성() {
		Product product = new Product();

		String productName = "강정치킨";
		BigDecimal productPrice = new BigDecimal("17000.00");

		product.setName(productName);
		product.setPrice(productPrice);

		return productDao.save(product);
	}
}