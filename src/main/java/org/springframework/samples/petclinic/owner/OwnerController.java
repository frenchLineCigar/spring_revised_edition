/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import org.springframework.context.ApplicationContext;
import org.springframework.samples.petclinic.visit.VisitRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

	private final OwnerRepository owners;

	private VisitRepository visits;

	private final ApplicationContext applicationContext;

	public OwnerController(OwnerRepository clinicService, VisitRepository visits, ApplicationContext applicationContext) {
		this.owners = clinicService;
		this.visits = visits;
		this.applicationContext = applicationContext;
	}

	@GetMapping("/bean") //요청을 핸들러로 맵핑해주는 애노테이션
	@ResponseBody //리턴 타입이 응답의 본문이 되도록 해주는 애노테이션
	public String bean() {
		// 애플리케이션 컨텍스트가 담고있던 OwnerController 인스턴스의 해쉬값 출력
		// 둘은 같은 인스턴스
		// 스프링 IoC 컨테이너는 싱글톤 스코프(유효범위)의 객체를 보장
		// 매번 새로 만드는 것이 아니라 어떤 객체 인스턴스 하나를 애플리케이션 전반에서 계속해서 재사용을 보장
		// 특히 Multi Thread 상황에서 싱글톤 스코프를 구현하는 것 자체는 굉장히 번거롭고 조심스러운 일인데
		// 그런 일을 IoC 컨테이너를 사용하면 굉장히 손쉽게 소스 코드에 아무런 특별한 코드를 넣지 않아도
		// IoC 컨테이너에 등록되어 있는 Bean을 가져다 쓰는 식으로 소스 코드를 만들면 굉장히 편하게 싱글톤 스코프를 달성할 수 있다.
		// 이것 역시 IoC 컨테이너를 사용하는 이유 중 하나이다.
		return "bean: " + applicationContext.getBean(OwnerRepository.class) + "\n"
				+ "owners: " + this.owners;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("/owners/new")
	public String initCreationForm(Map<String, Object> model) {
		Owner owner = new Owner();
		model.put("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid Owner owner, BindingResult result) {
		//BindingResult 아규먼트는 바인딩 시 Validation 에러를 검증, 모델에 담아준다(스프링이 기본으로 제공)
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			this.owners.save(owner);
			return "redirect:/owners/" + owner.getId();
		}
	}

	@GetMapping("/owners/find")
	public String initFindForm(Map<String, Object> model) {
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(Owner owner, BindingResult result, Map<String, Object> model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getFirstName() == null) {
			owner.setFirstName(""); // empty string signifies broadest possible search
		}

		// find owners by first name
		Collection<Owner> results = this.owners.findByFirstName(owner.getFirstName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		}
		else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		}
		else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, Model model) {
		Owner owner = this.owners.findById(ownerId);
		model.addAttribute(owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		else {
			owner.setId(ownerId);
			this.owners.save(owner);
			return "redirect:/owners/{ownerId}";
		}
	}

	/**
	 * Custom handler for displaying an owner.
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	/* GET /owners/11 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");
		/* 오너 정보 */
		Owner owner = this.owners.findById(ownerId);
		/* 오너가 가진 펫의 방문 정보*/
		for (Pet pet : owner.getPets()) {
			pet.setVisitsInternal(visits.findByPetId(pet.getId()));
		}
		mav.addObject(owner);
		return mav;
	}

}
